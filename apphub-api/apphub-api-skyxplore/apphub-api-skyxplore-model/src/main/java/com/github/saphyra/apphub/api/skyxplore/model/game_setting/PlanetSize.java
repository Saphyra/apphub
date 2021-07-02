package com.github.saphyra.apphub.api.skyxplore.model.game_setting;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PlanetSize {
    SMALL,
    MEDIUM,
    LARGE,
    RANDOM;

    @JsonCreator
    public static PlanetSize forValue(String value) {
        for (PlanetSize planetSize : values()) {
            if (planetSize.name().equalsIgnoreCase(value)) {
                return planetSize;
            }
        }

        return null;
    }
}
