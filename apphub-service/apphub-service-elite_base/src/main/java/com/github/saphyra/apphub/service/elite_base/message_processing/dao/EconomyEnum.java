package com.github.saphyra.apphub.service.elite_base.message_processing.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum EconomyEnum {
    REFINERY("Refinery"),
    INDUSTRIAL("Industrial"),
    AGRICULTURE("Agri"),
    COLONY("Colony"),
    MILITARY("Military"),
    HIGH_TECH("HighTech"),
    ENGINEER("Engineer"),
    EXTRACTION("Extraction"),
    TOURISM("Tourism"),
    REPAIR("Repair"),
    TERRAFORMING("Terraforming"),
    CARRIER("Carrier"),
    SERVICE("Service"),
    PRISON("Prison"),
    RESCUE("Rescue"),
    MEGA_SHIP("MegaShip"),
    NONE("None"),
    ;

    @Getter
    private final String value;

    public static EconomyEnum parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        String parsed = in.contains("_") ? in.split("_")[1].split(";")[0] : in;

        return Arrays.stream(values())
            .filter(allegiance -> allegiance.value.equalsIgnoreCase(parsed))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + EconomyEnum.class.getSimpleName() + ". Parsed: " + parsed));
    }
}
