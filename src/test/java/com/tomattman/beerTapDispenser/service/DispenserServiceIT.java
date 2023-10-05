package com.tomattman.beerTapDispenser.service;


import com.tomattman.beerTapDispenser.dto.DispenserStatisticDto;
import com.tomattman.beerTapDispenser.dto.DispenserStatusDto;
import com.tomattman.beerTapDispenser.dto.DispenserUsageDto;
import com.tomattman.beerTapDispenser.exception.DispenserNotFountException;
import com.tomattman.beerTapDispenser.exception.DispenserStatusException;
import com.tomattman.beerTapDispenser.model.Dispenser;
import com.tomattman.beerTapDispenser.model.DispenserStatus;
import com.tomattman.beerTapDispenser.model.DispenserUsage;
import com.tomattman.beerTapDispenser.repository.DispenserRepository;
import com.tomattman.beerTapDispenser.repository.DispenserUsageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@SpringBootTest
public class DispenserServiceIT {
    @Autowired
    private DispenserService dispenserService;
    @Autowired
    private DispenserRepository dispenserRepository;
    @Autowired
    private DispenserUsageRepository dispenserUsageRepository;

    @Value("${flow.unit.price}")
    private double priceForUnit;

    @Test
    void getDispenserById() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(0.001);
        dispenserRepository.save(dispenser);

        Dispenser dispenserFromDb = dispenserService.getDispenserById(dispenser.getId());

        Assertions.assertNotNull(dispenserFromDb);
        Assertions.assertNotNull(dispenserFromDb.getId());
        Assertions.assertEquals(dispenser.getFlowVolume(), dispenserFromDb.getFlowVolume());
    }

    @Test
    void getDispenserById_throwDispenserNotFoundException_dispenserNotExist(){
        Assertions.assertThrows(DispenserNotFountException.class, () -> dispenserService.getDispenserById(111L));
    }

    @Test
    void switchDispenserStatus_createNewDispenserUsageRow_statusIsOpen() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusDto = new DispenserStatusDto();
        dispenserStatusDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserStatusDto.setUpdatedAt(LocalDateTime.now());

        DispenserUsage dispenserUsage = dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusDto);

        Optional<DispenserUsage> dispenserUsageFromDb = dispenserUsageRepository.findById(dispenserUsage.getId());

        Assertions.assertTrue(dispenserUsageFromDb.isPresent());
        Assertions.assertEquals(dispenserStatusDto.getUpdatedAt(), dispenserUsageFromDb.get().getOpenTime());
        Assertions.assertNull(dispenserUsageFromDb.get().getCloseTime());
    }

    @Test
    void switchDispenserStatus_createNewDispenserUsageRowWithOpenTime_statusIsOpenAndOpenTimeIsNotPresented() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusDto = new DispenserStatusDto();
        dispenserStatusDto.setDispenserStatus(DispenserStatus.OPEN);

        DispenserUsage dispenserUsage = dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusDto);

        Optional<DispenserUsage> dispenserUsageFromDb = dispenserUsageRepository.findById(dispenserUsage.getId());

        Assertions.assertTrue(dispenserUsageFromDb.isPresent());
        Assertions.assertNotNull(dispenserUsageFromDb.get().getOpenTime());
        Assertions.assertNull(dispenserUsageFromDb.get().getCloseTime());
    }

    @Test
    void switchDispenserStatus_throwDispenserStatusException_dispenserIsOpen() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusDto = new DispenserStatusDto();
        dispenserStatusDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusDto);

        DispenserStatusDto dispenserStatusDto1 = new DispenserStatusDto();
        dispenserStatusDto1.setDispenserStatus(DispenserStatus.OPEN);
        Assertions.assertThrows(DispenserStatusException.class, () -> dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusDto));
    }

    @Test
    void switchDispenserStatus_throwDispenserNotFoundException_dispenserIsNotExist() {
        DispenserStatusDto dispenserStatusDto = new DispenserStatusDto();
        dispenserStatusDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserService.switchDispenserStatus(123L, dispenserStatusDto);

        Assertions.assertThrows(DispenserNotFountException.class, () -> dispenserService.switchDispenserStatus(123L, dispenserStatusDto));
    }

    @Test
    void switchDispenserStatus_throwDispenserStatusException_openDateIsInFuture() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusDto = new DispenserStatusDto();
        dispenserStatusDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserStatusDto.setUpdatedAt(LocalDateTime.now().plusDays(5));
        Assertions.assertThrows(DispenserStatusException.class, () -> dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusDto));
    }

    @Test
    void switchDispenserStatus_throwDispenserStatusException_closeNotOpenedDispenser() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusDto = new DispenserStatusDto();
        dispenserStatusDto.setDispenserStatus(DispenserStatus.CLOSE);
        Assertions.assertThrows(DispenserStatusException.class, () -> dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusDto));
    }

    @Test
    void switchDispenserStatus_updateExistRow_dispenserIsOpen() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusOpenDto = new DispenserStatusDto();
        dispenserStatusOpenDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserStatusOpenDto.setUpdatedAt(LocalDateTime.now().minusMinutes(4));
        dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusOpenDto);

        DispenserStatusDto dispenserStatusCloseDto = new DispenserStatusDto();
        dispenserStatusCloseDto.setDispenserStatus(DispenserStatus.CLOSE);
        dispenserStatusCloseDto.setUpdatedAt(LocalDateTime.now().minusMinutes(3));
        DispenserUsage dispenserUsage = dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusCloseDto);

        Optional<DispenserUsage> dispenserFromDb = dispenserUsageRepository.findById(dispenserUsage.getId());
        Assertions.assertTrue(dispenserFromDb.isPresent());
        Assertions.assertNotNull(dispenserFromDb.get().getCloseTime());
        Assertions.assertNotNull(dispenserFromDb.get().getFlowVolume());
        Assertions.assertEquals(dispenser.getFlowVolume() * ChronoUnit.SECONDS.between(dispenserStatusOpenDto.getUpdatedAt(), dispenserStatusCloseDto.getUpdatedAt()), dispenserFromDb.get().getFlowVolume());
        Assertions.assertNotNull(dispenserFromDb.get().getTotalSpent());
        Assertions.assertEquals(dispenserFromDb.get().getFlowVolume() * priceForUnit, dispenserFromDb.get().getTotalSpent().doubleValue());

    }

    @Test
    void switchDispenserStatus_throwDispenserStatusException_closeDateIsInFuture() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusOpenDto = new DispenserStatusDto();
        dispenserStatusOpenDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserStatusOpenDto.setUpdatedAt(LocalDateTime.now().minusMinutes(4));
        dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusOpenDto);

        DispenserStatusDto dispenserStatusCloseDto = new DispenserStatusDto();
        dispenserStatusCloseDto.setDispenserStatus(DispenserStatus.CLOSE);
        dispenserStatusCloseDto.setUpdatedAt(LocalDateTime.now().plusMinutes(3));
        Assertions.assertThrows(DispenserStatusException.class, () -> dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusCloseDto));

    }

    @Test
    void switchDispenserStatus_throwDispenserStatusException_closeDateIsBeforeOpenDate() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserStatusDto dispenserStatusOpenDto = new DispenserStatusDto();
        dispenserStatusOpenDto.setDispenserStatus(DispenserStatus.OPEN);
        dispenserStatusOpenDto.setUpdatedAt(LocalDateTime.now().minusMinutes(4));
        dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusOpenDto);

        DispenserStatusDto dispenserStatusCloseDto = new DispenserStatusDto();
        dispenserStatusCloseDto.setDispenserStatus(DispenserStatus.CLOSE);
        dispenserStatusCloseDto.setUpdatedAt(LocalDateTime.now().minusDays(3));
        Assertions.assertThrows(DispenserStatusException.class, () -> dispenserService.switchDispenserStatus(dispenser.getId(), dispenserStatusCloseDto));

    }

    @Test
    void getDispenserStatistic_throwsDispenserNotFoundException_dispenserNotExist() {
        Assertions.assertThrows(DispenserNotFountException.class, () -> dispenserService.getDispenserStatistic(234L));
    }

    @Test
    void getDispenserStatistic() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserUsage usage = new DispenserUsage();
        usage.setOpenTime(LocalDateTime.now());
        usage.setCloseTime(LocalDateTime.now());
        usage.setDispenser(dispenser);
        usage.setFlowVolume(1.0);
        usage.setTotalSpent(new BigDecimal(999));
        dispenserUsageRepository.save(usage);

        DispenserUsage usage1 = new DispenserUsage();
        usage1.setOpenTime(LocalDateTime.now());
        usage1.setCloseTime(LocalDateTime.now());
        usage1.setDispenser(dispenser);
        usage1.setFlowVolume(2.0);
        usage1.setTotalSpent(new BigDecimal(111));
        dispenserUsageRepository.save(usage1);

        DispenserStatisticDto dispenserStatistic = dispenserService.getDispenserStatistic(dispenser.getId());

        Assertions.assertNotNull(dispenserStatistic);
        Assertions.assertEquals(usage.getTotalSpent().add(usage1.getTotalSpent()), dispenserStatistic.getAmount());
        Assertions.assertNotNull(dispenserStatistic.getUsages());
        Assertions.assertEquals(2, dispenserStatistic.getUsages().size());

    }

    @Test
    void getDispenserStatistic_containsNotClosedUsagesWithCountedPrice() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserUsage usage = new DispenserUsage();
        usage.setOpenTime(LocalDateTime.now());
        usage.setCloseTime(LocalDateTime.now());
        usage.setDispenser(dispenser);
        usage.setFlowVolume(1.0);
        usage.setTotalSpent(new BigDecimal(999));
        dispenserUsageRepository.save(usage);

        DispenserUsage usage1 = new DispenserUsage();
        usage1.setOpenTime(LocalDateTime.now().minusSeconds(10));
        usage1.setDispenser(dispenser);
        dispenserUsageRepository.save(usage1);

        DispenserStatisticDto dispenserStatistic = dispenserService.getDispenserStatistic(dispenser.getId());

        Assertions.assertNotNull(dispenserStatistic);
        Assertions.assertNotNull(dispenserStatistic.getAmount());
        Assertions.assertTrue(dispenserStatistic.getAmount().compareTo(usage.getTotalSpent()) > 0);
        Assertions.assertNotNull(dispenserStatistic.getUsages());
        Assertions.assertEquals(2, dispenserStatistic.getUsages().size());
        for(DispenserUsageDto resultUsage: dispenserStatistic.getUsages()) {
            Assertions.assertNotNull(resultUsage.getFlowVolume());
            Assertions.assertNotNull(resultUsage.getTotalSpent());
        }

    }

}
