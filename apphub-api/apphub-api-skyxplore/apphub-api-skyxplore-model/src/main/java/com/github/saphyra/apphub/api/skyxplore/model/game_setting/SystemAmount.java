package com.github.saphyra.apphub.api.skyxplore.model.game_setting;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SystemAmount {
    SMALL,
    MEDIUM,
    COMMON,
    RANDOM;

    @JsonCreator
    public static SystemAmount forValues(String value) {
        for (SystemAmount systemAmount : values()) {
            if (systemAmount.name().equalsIgnoreCase(value)) {
                return systemAmount;
            }
        }

        return null;
    }
}
