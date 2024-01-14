package com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.Vector;

public class SolarSystems extends Vector<SolarSystem> {
    public SolarSystem findByIdValidated(UUID solarSystemId) {
        return stream()
            .filter(solarSystem -> solarSystem.getSolarSystemId().equals(solarSystemId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "SolarSystem not found with id " + solarSystemId));
    }
}
