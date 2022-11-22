package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PriorityType {
    CONSTRUCTION, //Building new buildings/terraformation
    INDUSTRY, //Producing ships, and equipment
    WELL_BEING; //Eating, Morale, Hit Points

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
