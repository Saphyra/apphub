package com.github.saphyra.apphub.api.skyxplore.model.game_setting;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UniverseSize {
    SMALLEST,
    SMALL,
    MEDIUM,
    LARGE;

    @JsonCreator
    public static UniverseSize forValue(String value) {
        for (UniverseSize universeSize : values()) {
            if (universeSize.name().equalsIgnoreCase(value)) {
                return universeSize;
            }
        }

        return null;
    }
}
