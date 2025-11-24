package com.github.saphyra.apphub.api.custom.elite_base.model;

import static java.util.Objects.isNull;

public enum MaterialType {
    ANY,
    UNKNOWN,
    MANUFACTURED,
    ENCODED,
    RAW,
    NONE,
    ;

    public static MaterialType parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return valueOf(in);
    }
}
