package com.github.saphyra.apphub.service.elite_base.dao;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum Allegiance {
    FEDERATION("Federation"),
    EMPIRE("Empire"),
    ALLIANCE("Alliance"),
    INDEPENDENT("Independent"),
    THARGOID("Thargoid"),
    PILOTS_FEDERATION("PilotsFederation"),
    GUARDIAN("Guardian"),
    NONE(""),
    ;

    private final String value;

    //TODO unit test
    public static Allegiance parse(String in) {
        if (isBlank(in)) {
            return Allegiance.NONE;
        }

        return Arrays.stream(values())
            .filter(allegiance -> allegiance.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + Allegiance.class.getSimpleName()));
    }
}
