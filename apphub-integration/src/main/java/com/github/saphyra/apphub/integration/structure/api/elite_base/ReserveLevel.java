package com.github.saphyra.apphub.integration.structure.api.elite_base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum ReserveLevel {
    UNKNOWN("N/A" ,0),
    DEPLETED("DepletedResources" ,1),
    LOW("LowResources", 2),
    COMMON("CommonResources", 3),
    MAJOR("MajorResources", 4),
    PRISTINE("PristineResources", 5),
    ;

    @Getter
    private final String value;
    @Getter
    private final int level;

    public static ReserveLevel parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(reserveLevel -> reserveLevel.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + ReserveLevel.class.getSimpleName()));
    }
}
