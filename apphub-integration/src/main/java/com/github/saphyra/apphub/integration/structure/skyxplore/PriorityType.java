package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PriorityType {
    CONSTRUCTION,
    INDUSTRY,
    WELL_BEING;

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
