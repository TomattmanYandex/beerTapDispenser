package com.tomattman.beerTapDispenser.repository;

import com.tomattman.beerTapDispenser.model.Dispenser;
import com.tomattman.beerTapDispenser.model.DispenserUsage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DispenserUsageRepositoryTest {
    @Autowired
    private DispenserRepository dispenserRepository;
    @Autowired
    private DispenserUsageRepository dispenserUsageRepository;

    @Test
    void findByDispenserIdAndCloseTimeIsNull() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserUsage usage = new DispenserUsage();
        usage.setDispenser(dispenser);
        usage.setOpenTime(LocalDateTime.now());
        usage.setTotalSpent(BigDecimal.valueOf(11.11));
        usage.setFlowVolume(1.1);
        dispenserUsageRepository.save(usage);

        Optional<DispenserUsage> result = dispenserUsageRepository.findByDispenserIdAndCloseTimeIsNull(dispenser.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getCloseTime());
        Assertions.assertNotNull(result.get().getOpenTime());
        Assertions.assertEquals(usage.getOpenTime(), result.get().getOpenTime());
        Assertions.assertEquals(usage.getTotalSpent(), result.get().getTotalSpent());
        Assertions.assertEquals(usage.getFlowVolume(), result.get().getFlowVolume());
    }

    @Test
    void findByDispenserIdAndCloseTimeIsNull_empty_rowsNotExist() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserUsage usage = new DispenserUsage();
        usage.setDispenser(dispenser);
        usage.setOpenTime(LocalDateTime.now());
        usage.setCloseTime(LocalDateTime.now());
        usage.setTotalSpent(BigDecimal.valueOf(11.11));
        usage.setFlowVolume(1.1);
        dispenserUsageRepository.save(usage);

        Optional<DispenserUsage> result = dispenserUsageRepository.findByDispenserIdAndCloseTimeIsNull(dispenser.getId());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByDispenserIdAndCloseTimeIsNull_returnCorrect_notOneDispenser() {
        Dispenser dispenser = new Dispenser();
        dispenser.setFlowVolume(1d);
        dispenserRepository.save(dispenser);

        DispenserUsage usage = new DispenserUsage();
        usage.setDispenser(dispenser);
        usage.setOpenTime(LocalDateTime.now());
        usage.setTotalSpent(BigDecimal.valueOf(11.11));
        usage.setFlowVolume(1.1);
        dispenserUsageRepository.save(usage);

        Dispenser dispenser1 = new Dispenser();
        dispenser1.setFlowVolume(1d);
        dispenserRepository.save(dispenser1);

        DispenserUsage usage1 = new DispenserUsage();
        usage1.setDispenser(dispenser1);
        usage1.setOpenTime(LocalDateTime.now());
        usage1.setTotalSpent(BigDecimal.valueOf(2222.222));
        usage1.setFlowVolume(111.1);
        dispenserUsageRepository.save(usage1);

        Optional<DispenserUsage> result = dispenserUsageRepository.findByDispenserIdAndCloseTimeIsNull(dispenser1.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getCloseTime());
        Assertions.assertNotNull(result.get().getOpenTime());
        Assertions.assertEquals(usage1.getOpenTime(), result.get().getOpenTime());
        Assertions.assertEquals(usage1.getTotalSpent(), result.get().getTotalSpent());
        Assertions.assertEquals(usage1.getFlowVolume(), result.get().getFlowVolume());
    }

}
