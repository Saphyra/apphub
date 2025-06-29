package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum ReserveLevel {
    DEPLETED("DepletedResources"),
    LOW("LowResources"),
    COMMON("CommonResources"),
    MAJOR("MajorResources"),
    PRISTINE("PristineResources"),
    ;

    @Getter
    private final String value;

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
