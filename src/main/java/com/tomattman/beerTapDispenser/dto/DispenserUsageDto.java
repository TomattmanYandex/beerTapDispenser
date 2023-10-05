package com.tomattman.beerTapDispenser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DispenserUsageDto {
    @JsonProperty("opened_at")
    private LocalDateTime openedAt;
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    @JsonProperty("flow_volume")
    private Double flowVolume;
    @JsonProperty("total_spent")
    private BigDecimal totalSpent;

}
