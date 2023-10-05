package com.tomattman.beerTapDispenser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DispenserDto {
    private Long id;
    @JsonProperty("flow_volume")
    private Double flowVolume;
}
