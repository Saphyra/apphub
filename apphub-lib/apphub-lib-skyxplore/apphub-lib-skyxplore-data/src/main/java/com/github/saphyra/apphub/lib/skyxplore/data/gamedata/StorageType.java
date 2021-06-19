package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum StorageType {
    CITIZEN, BULK, LIQUID, ENERGY;

    @JsonCreator
    public static StorageType fromValue(String value) {
        return Arrays.stream(values())
            .filter(storageType -> storageType.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("StorageType cannot be parsed from %s.", value)));
    }
}
