package com.github.saphyra.apphub.service.custom.elite_base.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

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
    DAMAGED("Damaged"),
    NONE("None"),
    ;

    @Getter
    private final String value;

    public static EconomyEnum parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        String parsed = in.contains("_") ? in.split("_")[1].split(";")[0] : in;

        if (parsed.equalsIgnoreCase("Undefined")) {
            return EconomyEnum.NONE;
        }

        return Arrays.stream(values())
            .filter(allegiance -> allegiance.value.equalsIgnoreCase(parsed))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + EconomyEnum.class.getSimpleName() + ". Parsed: " + parsed));
    }

    public static EconomyEnum fromValue(String in) {
        if (isNull(in)) {
            return null;
        }
        return valueOf(in);
    }
}
