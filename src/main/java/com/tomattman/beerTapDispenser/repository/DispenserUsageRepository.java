package com.tomattman.beerTapDispenser.repository;

import com.tomattman.beerTapDispenser.model.DispenserUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispenserUsageRepository extends JpaRepository<DispenserUsage, Long> {
    Optional<DispenserUsage> findByDispenserIdAndCloseTimeIsNull(Long id);
    List<DispenserUsage> findByDispenserId(Long id);
}
