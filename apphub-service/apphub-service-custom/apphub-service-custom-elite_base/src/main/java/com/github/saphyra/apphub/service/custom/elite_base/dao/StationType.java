package com.github.saphyra.apphub.service.custom.elite_base.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.LandingPad;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public enum StationType {
    @Deprecated(forRemoval = true)
    SETTLEMENT("", null),
    ON_FOOT_SETTLEMENT("OnFootSettlement", null),
    ORBIS("Orbis", LandingPad.LARGE),
    CORIOLIS("Coriolis", LandingPad.LARGE),
    BERNAL("Bernal", null),
    CRATER_OUTPOST("CraterOutpost", LandingPad.MEDIUM),
    CRATER_PORT("CraterPort", LandingPad.LARGE),
    FLEET_CARRIER("FleetCarrier", LandingPad.LARGE),
    OUTPOST("Outpost", LandingPad.MEDIUM),
    ASTEROID_BASE("AsteroidBase", null),
    MEGA_SHIP("MegaShip", LandingPad.LARGE),
    OCELLUS("Ocellus", LandingPad.LARGE),
    SURFACE_STATION("SurfaceStation", null),
    PLANETARY_CONSTRUCTION_DEPOT("PlanetaryConstructionDepot", LandingPad.LARGE),
    UNKNOWN("Unknown", null),
    SPACE_CONSTRUCTION_DEPOT("SpaceConstructionDepot", LandingPad.LARGE),
    DOCKABLE_PLANET_STATION("DockablePlanetStation", null),
    GAMEPLAY_POI("GameplayPOI", null),
    ;

    @Getter
    private final String value;
    @Getter
    private final LandingPad landingPad;

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
