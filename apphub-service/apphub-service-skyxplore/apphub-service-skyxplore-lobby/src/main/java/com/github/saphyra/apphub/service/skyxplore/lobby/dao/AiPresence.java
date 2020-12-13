package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AiPresence {
    NONE,
    RARE,
    COMMON,
    EVERYWHERE;

    @JsonCreator
    public static AiPresence forValues(String value) {
        for (AiPresence aiPresence : values()) {
            if (aiPresence.name().equalsIgnoreCase(value)) {
                return aiPresence;
            }
        }

        return null;
    }
}
