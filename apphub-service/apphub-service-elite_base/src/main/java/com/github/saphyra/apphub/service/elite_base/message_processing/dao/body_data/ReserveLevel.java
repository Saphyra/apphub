package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data;

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

    private final String value;

    //TODO unit test
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
