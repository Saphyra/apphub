package com.github.saphyra.apphub.service.elite_base.dao;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum StationType {
    SETTLEMENT(""),
    ON_FOOT_SETTLEMENT("OnFootSettlement"),
    ORBIS("Orbis"),
    CORIOLIS("Coriolis"),
    BERNAL("Bernal"),
    CRATER_OUTPOST("CraterOutpost"),
    CRATER_PORT("CraterPort"),
    FLEET_CARRIER("FleetCarrier"),
    OUTPOST("Outpost"),
    ASTEROID_BASE("AsteroidBase"),
    MEGA_SHIP("MegaShip"),
    ;

    private final String value;

    //TODO unit test
    public static StationType parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in))
            .findAny()
            .orElseThrow(() -> new RuntimeException(in + " cannot be parsed to " + StationType.class.getSimpleName()));
    }
}
