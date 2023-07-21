package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PriorityType {
    CONSTRUCTION,
    INDUSTRY,
    WELL_BEING,
    EDUCATION;

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
