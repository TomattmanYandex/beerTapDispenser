package com.tomattman.beerTapDispenser.mapper;

import com.tomattman.beerTapDispenser.dto.DispenserDto;
import com.tomattman.beerTapDispenser.model.Dispenser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DispenserMapper {
    Dispenser toEntity(DispenserDto dispenserDto);
    DispenserDto toDto(Dispenser dispenser);
}
