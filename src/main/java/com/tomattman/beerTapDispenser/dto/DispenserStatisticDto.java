package com.tomattman.beerTapDispenser.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DispenserStatisticDto {
    private BigDecimal amount;
    private List<DispenserUsageDto> usages;
 }
