package com.tomattman.beerTapDispenser.repository;

import com.tomattman.beerTapDispenser.model.Dispenser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispenserRepository extends JpaRepository<Dispenser, Long> {
}
