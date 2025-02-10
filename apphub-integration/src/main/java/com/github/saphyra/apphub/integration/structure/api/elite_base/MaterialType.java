package com.github.saphyra.apphub.integration.structure.api.elite_base;

import static java.util.Objects.isNull;

public enum MaterialType {
    ANY,
    UNKNOWN,
    MANUFACTURED,
    ENCODED,
    RAW;

    public static MaterialType parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return valueOf(in);
    }
}
