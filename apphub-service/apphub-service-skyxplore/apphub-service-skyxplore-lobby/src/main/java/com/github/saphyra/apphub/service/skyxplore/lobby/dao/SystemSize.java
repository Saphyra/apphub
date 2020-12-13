package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SystemSize {
    SMALL,
    MEDIUM,
    LARGE,
    RANDOM;

    @JsonCreator
    public static SystemSize forValues(String value) {
        for (SystemSize systemSize : values()) {
            if (systemSize.name().equalsIgnoreCase(value)) {
                return systemSize;
            }
        }

        return null;
    }
}
