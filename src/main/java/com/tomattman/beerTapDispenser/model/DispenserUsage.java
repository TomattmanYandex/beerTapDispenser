package com.tomattman.beerTapDispenser.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class DispenserUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="dispenser_id", nullable=false)
    private Dispenser dispenser;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Double flowVolume;
    private BigDecimal totalSpent;

}
