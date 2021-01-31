package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PriorityType {
    CONSTRUCTION,
    MANUFACTURING,
    EXTRACTION;

    @JsonCreator
    public static PriorityType fromValue(String value) {
        for (PriorityType priorityType : values()) {
            if (priorityType.name().equalsIgnoreCase(value)) {
                return priorityType;
            }
        }

        return null;
    }
}