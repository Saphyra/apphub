package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class AssignCitizenService {
    private final TickCache tickCache;
    private final int workPointsPerTick;

    public AssignCitizenService(
        TickCache tickCache,
        GameProperties gameProperties
    ) {
        this.tickCache = tickCache;
        this.workPointsPerTick = gameProperties.getCitizen().getWorkPointsPerSeconds();
    }

    public Assignment assignCitizen(UUID gameId, Citizen citizen, UUID location) {
        Assignment assignment = Assignment.builder()
            .citizen(citizen)
            .location(location)
            .workPointsLeft(workPointsPerTick)
            .build();
        log.debug("{} created for game {}", assignment, gameId);

        tickCache.get(gameId)
            .getCitizenAssignments()
            .put(citizen.getCitizenId(), assignment);

        return assignment;
    }
}
