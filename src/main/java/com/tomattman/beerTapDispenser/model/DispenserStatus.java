package com.tomattman.beerTapDispenser.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DispenserStatus {
    OPEN, CLOSE;

    @JsonCreator
    public static DispenserStatus fromString(String key) {
        return key == null ? null : DispenserStatus.valueOf(key.toUpperCase());
    }
}
