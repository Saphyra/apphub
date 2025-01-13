package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

public enum StarType {
    A,
    AEBE,
    B,
    D,
    DA,
    DAB,
    DC,
    DQ,
    F,
    G,
    H,
    K,
    L,
    M,
    N,
    O,
    Y,
    T,
    TTS,
    ;

    public static StarType parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        String parsed = in.split("_")[0];

        return Arrays.stream(values())
            .filter(e -> e.name().equalsIgnoreCase(parsed))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + StarType.class.getSimpleName() + " Parsed: " + parsed));
    }
}
