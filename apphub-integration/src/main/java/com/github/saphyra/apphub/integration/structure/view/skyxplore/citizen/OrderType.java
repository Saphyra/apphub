package com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum OrderType {
    BY_NAME("by-name"),
    BY_STAT("by-stat"),
    BY_SKILL("by-skill"),
    ;

    private final String value;

    public static OrderType fromValue(String value) {
        return Arrays.stream(values())
            .filter(orderType -> orderType.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No orderType exists with value " + value));
    }
}
