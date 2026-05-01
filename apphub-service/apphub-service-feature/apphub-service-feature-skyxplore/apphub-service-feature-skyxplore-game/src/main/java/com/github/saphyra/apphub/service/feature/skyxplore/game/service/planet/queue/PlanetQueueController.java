package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.SkyXplorePlanetQueueController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetQueueController implements SkyXplorePlanetQueueController {
    private final QueueFacade queueFacade;

    @Override
    public void setItemPriority(OneParamRequest<Integer> priority, UUID planetId, String type, UUID itemId, AccessToken accessToken) {
        log.info("{} wants to set priority of {}/{} to {} on planet {}", accessToken.getUserId(), type, itemId, priority.getValue(), planetId);
        queueFacade.setPriority(accessToken.getUserId(), planetId, type, itemId, priority.getValue());
    }

    @Override
    public void cancelItem(UUID planetId, String type, UUID itemId, AccessToken accessToken) {
        log.info("{} wants to cancel queue item {}/{} on planet {}", accessToken.getUserId(), type, itemId, planetId);
        queueFacade.cancelItem(accessToken.getUserId(), planetId, type, itemId);
    }
}
