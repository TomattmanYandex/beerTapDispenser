package com.tomattman.beerTapDispenser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomattman.beerTapDispenser.converter.DispenserStatusConverter;
import com.tomattman.beerTapDispenser.model.DispenserStatus;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class DispenserStatusDto {
    @JsonProperty("status")
    private DispenserStatus dispenserStatus;
    @JsonProperty("updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;
}
