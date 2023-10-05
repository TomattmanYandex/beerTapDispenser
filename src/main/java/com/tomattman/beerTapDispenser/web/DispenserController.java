package com.tomattman.beerTapDispenser.web;

import com.tomattman.beerTapDispenser.dto.DispenserDto;
import com.tomattman.beerTapDispenser.dto.DispenserStatisticDto;
import com.tomattman.beerTapDispenser.dto.DispenserStatusDto;
import com.tomattman.beerTapDispenser.mapper.DispenserMapper;
import com.tomattman.beerTapDispenser.model.Dispenser;
import com.tomattman.beerTapDispenser.service.DispenserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dispenser")
@RequiredArgsConstructor
@Slf4j
public class DispenserController {

    private final DispenserService dispenserService;
    private final DispenserMapper dispenserMapper;

    @PostMapping
    public ResponseEntity<DispenserDto> createDispenser(@RequestBody DispenserDto dispenserDto) {
        Dispenser dispenser = dispenserService.createDispenser(dispenserMapper.toEntity(dispenserDto));
        log.info("Created new dispenser {}", dispenser);

        return ResponseEntity.ok(dispenserMapper.toDto(dispenser));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> switchDispenserStatus(@PathVariable Long id, @RequestBody DispenserStatusDto dispenserStatus) {
        log.info("Change dispenser status to {}. Dispenser id - {}", dispenserStatus.getDispenserStatus(), id);
        dispenserService.switchDispenserStatus(id, dispenserStatus);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}/spending")
    public ResponseEntity<DispenserStatisticDto> getDispenserStatistic(@PathVariable Long id) {
        log.info("Get statistic by dispenser with id - {}", id);
        return ResponseEntity.ok(dispenserService.getDispenserStatistic(id));
    }

}
