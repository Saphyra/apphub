package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PlanetSize {
    SMALL,
    MEDIUM,
    LARGE,
    RANDOM;

    @JsonCreator
    public static PlanetSize forValues(String value) {
        for (PlanetSize planetSize : values()) {
            if (planetSize.name().equalsIgnoreCase(value)) {
                return planetSize;
            }
        }

        return null;
    }
}
