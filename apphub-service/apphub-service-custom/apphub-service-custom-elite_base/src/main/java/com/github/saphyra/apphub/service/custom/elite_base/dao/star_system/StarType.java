package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum StarType {
    A,
    AEBE,
    B,
    C,
    CJ,
    CN,
    D,
    DA,
    DAB,
    DAV,
    DAZ,
    DB,
    DBV,
    DBZ,
    DCV,
    DC,
    DQ,
    F,
    G,
    H,
    K,
    L,
    M,
    MS,
    N,
    O,
    S,
    Y,
    T,
    TTS,
    W,
    WC,
    WN,
    WNC,
    WO,
    SUPERMASSIVE_BLACK_HOLE("SupermassiveBlackHole"),
    ;

    private final String value;

    StarType() {
        this.value = this.name();
    }

    public static StarType parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        String parsed = in.split("_")[0];

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(parsed))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + StarType.class.getSimpleName() + " Parsed: " + parsed));
    }
}
