package com.github.saphyra.apphub.service.custom.elite_base.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum SecurityLevel {
    ANARCHY("$GAlAXY_MAP_INFO_state_anarchy;"),
    LOW("$SYSTEM_SECURITY_low;"),
    MEDIUM("$SYSTEM_SECURITY_medium;"),
    HIGH("$SYSTEM_SECURITY_high;"),
    ;

    @Getter
    private final String value;

    public static SecurityLevel parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(allegiance -> allegiance.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + SecurityLevel.class.getSimpleName()));
    }
}
