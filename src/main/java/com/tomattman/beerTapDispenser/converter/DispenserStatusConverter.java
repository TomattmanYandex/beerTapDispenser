package com.tomattman.beerTapDispenser.converter;

import com.tomattman.beerTapDispenser.model.DispenserStatus;
import org.springframework.core.convert.converter.Converter;

public class DispenserStatusConverter implements Converter<String, DispenserStatus> {
    @Override
    public DispenserStatus convert(String source) {
        return DispenserStatus.valueOf(source.toUpperCase());
    }
}
