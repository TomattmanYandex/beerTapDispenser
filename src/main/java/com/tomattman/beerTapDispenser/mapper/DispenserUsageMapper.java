package com.tomattman.beerTapDispenser.mapper;

import com.tomattman.beerTapDispenser.dto.DispenserUsageDto;
import com.tomattman.beerTapDispenser.model.DispenserUsage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DispenserUsageMapper {
    @Mapping(target = "openedAt", source = "dispenserUsage.openTime")
    @Mapping(target = "closedAt", source = "dispenserUsage.closeTime")
    DispenserUsageDto toDto(DispenserUsage dispenserUsage);
    List<DispenserUsageDto> toDto(List<DispenserUsage> dispenserUsages);
}
