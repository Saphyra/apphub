package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum WarType {
    ELECTION("election"),
    CIVIL_WAR("civilwar"),
    WAR("war"),
    ;

    private final String value;

    //TODO unit test
    public static WarType parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + WarType.class.getSimpleName()));
    }
}
