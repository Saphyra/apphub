package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.SkyXplorePlanetPriorityController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetPriorityControllerImpl implements SkyXplorePlanetPriorityController {
    private final PriorityUpdateService priorityUpdateService;

    @Override
    public void updatePriority(OneParamRequest<Integer> newPriority, UUID planetId, String priorityType, AccessToken accessToken) {
        log.info("{} wants to update priority of {} on planet {}", accessToken.getUserId(), priorityType, planetId);
        priorityUpdateService.updatePriority(accessToken.getUserId(), planetId, priorityType, newPriority.getValue());
    }
}
