package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum PowerplayState {
    UNOCCUPIED("Unoccupied"),
    EXPLOITED("Exploited"),
    FORTIFIED("Fortified"),
    STRONGHOLD("Stronghold"),
    CONTESTED("Contested"),
    ;
    private final String value;

    //TODO unit test
    public static PowerplayState parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + PowerplayState.class.getSimpleName()));
    }
}
