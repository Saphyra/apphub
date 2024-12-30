package com.github.saphyra.apphub.service.elite_base.dao.station.station_economy;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
public enum StationEconomyEnum {
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
    ;

    private final String value;

    //TODO unit test
    public static StationEconomyEnum parse(String in) {
        if (isBlank(in)) {
            return null;
        }

        String parsed = in.split("_")[1]
            .split(";")[0];

        return Arrays.stream(values())
            .filter(allegiance -> allegiance.value.equalsIgnoreCase(parsed))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Could not parse " + in + " to " + StationEconomyEnum.class.getSimpleName() + ". Parsed: " + parsed));
    }
}
