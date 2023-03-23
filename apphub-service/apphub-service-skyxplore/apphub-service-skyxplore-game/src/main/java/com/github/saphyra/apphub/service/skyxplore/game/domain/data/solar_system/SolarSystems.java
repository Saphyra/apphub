package com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system;

import java.util.UUID;
import java.util.Vector;

public class SolarSystems extends Vector<SolarSystem> {
    public SolarSystem findByIdValidated(UUID solarSystemId) {
        return stream()
            .filter(solarSystem -> solarSystem.getSolarSystemId().equals(solarSystemId))
            .findAny()
            .orElseThrow();
    }
}
