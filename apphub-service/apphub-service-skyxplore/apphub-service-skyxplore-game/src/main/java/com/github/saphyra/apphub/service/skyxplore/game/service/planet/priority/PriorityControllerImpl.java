package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXplorePriorityController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PriorityControllerImpl implements SkyXplorePriorityController {
    private final PriorityUpdateService priorityUpdateService;
    private final PriorityQueryService priorityQueryService;

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public Map<String, Integer> getPriorities(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the priorities of planet {}", accessTokenHeader.getUserId(), planetId);
        return priorityQueryService.getPriorities(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public void updatePriority(OneParamRequest<Integer> newPriority, UUID planetId, String priorityType, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to update priority of {} on planet {}", accessTokenHeader.getUserId(), priorityType, planetId);
        priorityUpdateService.updatePriority(accessTokenHeader.getUserId(), planetId, priorityType, newPriority.getValue());
    }
}
