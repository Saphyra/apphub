package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum SurfaceType {
    COAL_FIELD,
    CONCRETE,
    DESERT,
    FOREST,
    LAKE,
    MOUNTAIN,
    OIL_FIELD,
    ORE_FIELD,
    VOLCANO;

    @JsonCreator
    public static SurfaceType parse(String value) {
        return Arrays.stream(values())
            .filter(surfaceType -> surfaceType.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Value %s could not be parsed to SurfaceType", value)));
    }
}
