package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemResponseQueryService {
    private final GameDao gameDao;
    private final PlanetToLocationResponseConverter planetToLocationResponseConverter;
    private final VisibilityFacade visibilityFacade;

    SolarSystemResponse getSolarSystem(UUID userId, UUID solarSystemId) {
        Game game = gameDao.findByUserIdValidated(userId);
        SolarSystem solarSystem = game.getData()
            .getSolarSystems()
            .findByIdValidated(solarSystemId);

        if (!visibilityFacade.isVisible(game.getData(), userId, solarSystemId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.ITEM_NOT_VISIBLE, "SolarSystem " + solarSystemId + " is not visible for user " + userId);
        }

        List<PlanetLocationResponse> planets = planetToLocationResponseConverter.mapPlanets(game, solarSystemId, userId);
        return SolarSystemResponse.builder()
            .solarSystemId(solarSystemId)
            .systemName(solarSystem.getCustomNames().getOptional(userId).orElse(solarSystem.getDefaultName()))
            .radius(solarSystem.getRadius())
            .planets(planets)
            .build();
    }
}
