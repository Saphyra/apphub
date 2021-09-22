package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemResponseExtractor {
    private final VisibilityFacade visibilityFacade;

    List<MapSolarSystemResponse> getSolarSystems(UUID userId, Universe universe) {
        return universe.getSystems()
            .values()
            .stream()
            .filter(solarSystem -> visibilityFacade.isVisible(userId, solarSystem))
            .map(solarSystem -> MapSolarSystemResponse.builder()
                .solarSystemId(solarSystem.getSolarSystemId())
                .coordinate(solarSystem.getCoordinate().getCoordinate())
                .solarSystemName(solarSystem.getCustomNames().getOptional(userId).orElse(solarSystem.getDefaultName()))
                .planetNum(solarSystem.getPlanets().size())
                .build()
            )
            .collect(Collectors.toList());
    }
}
