package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionFactory {
    private final IdGenerator idGenerator;

    public Construction create(UUID buildingId, Integer requiredWorkPoints) {
        return Construction.builder()
            .constructionId(idGenerator.randomUuid())
            .location(buildingId)
            .locationType(LocationType.BUILDING)
            .requiredWorkPoints(requiredWorkPoints)
            .currentWorkPoints(0)
            .priority(GameConstants.DEFAULT_PRIORITY)
            .build();
    }
}
