package com.tomattman.beerTapDispenser.service;

import com.tomattman.beerTapDispenser.dto.DispenserStatisticDto;
import com.tomattman.beerTapDispenser.dto.DispenserStatusDto;
import com.tomattman.beerTapDispenser.exception.DispenserNotFountException;
import com.tomattman.beerTapDispenser.exception.DispenserStatusException;
import com.tomattman.beerTapDispenser.mapper.DispenserUsageMapper;
import com.tomattman.beerTapDispenser.model.Dispenser;
import com.tomattman.beerTapDispenser.model.DispenserUsage;
import com.tomattman.beerTapDispenser.model.DispenserStatus;
import com.tomattman.beerTapDispenser.repository.DispenserRepository;
import com.tomattman.beerTapDispenser.repository.DispenserUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispenserService {

    private final DispenserRepository dispenserRepository;
    private final DispenserUsageRepository dispenserUsageRepository;
    private final DispenserUsageMapper dispenserUsageMapper;

    @Value("${flow.unit.price}")
    private double priceForUnit;

    public Dispenser getDispenserById(Long id) {
        return dispenserRepository.findById(id).orElseThrow(() -> new DispenserNotFountException("Requested dispenser does not exist"));
    }

    public Dispenser createDispenser(Dispenser dispenser) {
        return dispenserRepository.save(dispenser);
    }

    public DispenserUsage switchDispenserStatus(Long dispenserId, DispenserStatusDto statusDto) {
        Dispenser dispenser = getDispenserById(dispenserId);
        Optional<DispenserUsage> dispenserUsage = dispenserUsageRepository.findByDispenserIdAndCloseTimeIsNull(dispenserId);
        validateDispenserStatusChange(dispenserUsage, statusDto);
        if (statusDto.getDispenserStatus() == DispenserStatus.OPEN) {
            return statusDto.getUpdatedAt() == null ? openDispenser(dispenser) : openDispenser(dispenser, statusDto.getUpdatedAt());
        } else {
            return statusDto.getUpdatedAt() == null ? closeDispenser(dispenser, dispenserUsage.get()) : closeDispenser(dispenser, dispenserUsage.get(), statusDto.getUpdatedAt());
        }
    }

    public DispenserStatisticDto getDispenserStatistic(Long dispenserId) {
        LocalDateTime requestTime = LocalDateTime.now();
        Dispenser dispenser = getDispenserById(dispenserId);
        List<DispenserUsage> usages = dispenserUsageRepository.findByDispenserId(dispenserId).stream().map(item -> getUsageWithNotClosedCalculation(dispenser, item, requestTime)).toList();
        BigDecimal sum = usages.stream().map(DispenserUsage::getTotalSpent).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        DispenserStatisticDto dispenserStatisticDto = new DispenserStatisticDto();
        dispenserStatisticDto.setAmount(sum);
        dispenserStatisticDto.setUsages(dispenserUsageMapper.toDto(usages));
        return dispenserStatisticDto;
    }

    private void validateDispenserStatusChange(Optional<DispenserUsage> currentUsage, DispenserStatusDto statusDto) {
        if (statusDto.getDispenserStatus() == DispenserStatus.OPEN && currentUsage.isPresent()) {
            log.warn("Attempt to open dispenser that is already open.");
            throw new DispenserStatusException("Attempt to open dispenser that is already open");
        }
        if (statusDto.getDispenserStatus() == DispenserStatus.CLOSE && currentUsage.isEmpty()) {
            log.warn("Attempt to close dispenser that is already closed");
            throw new DispenserStatusException("Attempt to close dispenser that is already closed");
        }
        if (statusDto.getUpdatedAt() != null && statusDto.getUpdatedAt().isAfter(LocalDateTime.now())) {
            log.warn("Attempt to change dispenser status in future");
            throw new DispenserStatusException("Attempt to change dispenser status in future");
        }
        if (statusDto.getDispenserStatus() == DispenserStatus.CLOSE && statusDto.getUpdatedAt() != null && currentUsage.get().getOpenTime().isAfter(statusDto.getUpdatedAt())) {
            log.warn("Attempt to close dispenser before opening");
            throw new DispenserStatusException("Attempt to close dispenser before opening");
        }
    }

    private DispenserUsage openDispenser(Dispenser dispenser, LocalDateTime openTime) {
        DispenserUsage dispenserUsage = new DispenserUsage();
        dispenserUsage.setDispenser(dispenser);
        dispenserUsage.setOpenTime(openTime);

        return dispenserUsageRepository.save(dispenserUsage);
    }

    private DispenserUsage openDispenser(Dispenser dispenser) {
        return openDispenser(dispenser, LocalDateTime.now());
    }

    private DispenserUsage closeDispenser(Dispenser dispenser, DispenserUsage currentUsage, LocalDateTime closeTime) {
        currentUsage.setCloseTime(closeTime);
        currentUsage.setFlowVolume(getFlowVolume(dispenser, currentUsage.getOpenTime(), currentUsage.getCloseTime()));
        currentUsage.setTotalSpent(getTotalValue(currentUsage.getFlowVolume()));
        return dispenserUsageRepository.save(currentUsage);
    }

    private DispenserUsage closeDispenser(Dispenser dispenser, DispenserUsage currentUsage) {
        return closeDispenser(dispenser, currentUsage, LocalDateTime.now());
    }

    private Double getFlowVolume(Dispenser dispenser, LocalDateTime openTime, LocalDateTime closeTime) {
        Long timeDifferenceInSeconds = ChronoUnit.SECONDS.between(openTime, closeTime);
        return dispenser.getFlowVolume() * timeDifferenceInSeconds;
    }

    private BigDecimal getTotalValue(Double totalVolume) {
        return BigDecimal.valueOf(totalVolume).multiply(BigDecimal.valueOf(priceForUnit));
    }

    private DispenserUsage getUsageWithNotClosedCalculation(Dispenser dispenser, DispenserUsage usage, LocalDateTime requestDate) {
        if (usage.getTotalSpent() != null) {
            return usage;
        } else {
            log.info("Requested statistic by opened dispenser");
            DispenserUsage notClosedUsage = new DispenserUsage();
            notClosedUsage.setOpenTime(usage.getOpenTime());
            notClosedUsage.setFlowVolume(getFlowVolume(dispenser, notClosedUsage.getOpenTime(), requestDate));
            notClosedUsage.setTotalSpent(getTotalValue(notClosedUsage.getFlowVolume()));
            return notClosedUsage;
        }
    }
}
