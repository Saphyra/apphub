package com.github.saphyra.apphub.api.skyxplore.model.game_setting;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SystemSize {
    SMALL,
    MEDIUM,
    LARGE,
    RANDOM;

    @JsonCreator
    public static SystemSize forValue(String value) {
        for (SystemSize systemSize : values()) {
            if (systemSize.name().equalsIgnoreCase(value)) {
                return systemSize;
            }
        }

        return null;
    }
}
