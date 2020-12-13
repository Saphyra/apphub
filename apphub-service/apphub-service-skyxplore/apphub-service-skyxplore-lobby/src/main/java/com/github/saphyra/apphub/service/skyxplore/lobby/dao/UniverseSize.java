package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UniverseSize {
    SMALLEST,
    SMALL,
    MEDIUM,
    LARGE;

    @JsonCreator
    public static UniverseSize forValues(String value) {
        for (UniverseSize universeSize : values()) {
            if (universeSize.name().equalsIgnoreCase(value)) {
                return universeSize;
            }
        }

        return null;
    }
}
