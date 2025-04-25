package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data;

import lombok.Getter;
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
    CONTROLLED("Controlled"),
    HOME_SYSTEM("HomeSystem"),
    PREPARED("Prepared"),
    IN_PREPARE_RADIUS("InPrepareRadius"),
    TURMOIL("Turmoil"),
    ;

    @Getter
    private final String value;

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
