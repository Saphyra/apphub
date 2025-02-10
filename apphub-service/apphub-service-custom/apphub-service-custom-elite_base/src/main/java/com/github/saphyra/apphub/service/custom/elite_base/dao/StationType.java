package com.github.saphyra.apphub.service.custom.elite_base.dao;

import lombok.Getter;
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
    OCELLUS("Ocellus"),
    SURFACE_STATION("SurfaceStation"),
    ;

    @Getter
    private final String value;

    public static StationType parse(String in) {
        if (isNull(in)) {
            return null;
        }

        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(in.replace(" ", "")))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(in + " cannot be parsed to " + StationType.class.getSimpleName()));
    }
}
