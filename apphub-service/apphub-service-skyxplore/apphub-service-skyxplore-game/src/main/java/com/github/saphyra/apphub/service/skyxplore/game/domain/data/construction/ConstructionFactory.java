package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionFactory {
    private final IdGenerator idGenerator;

    public Construction create(UUID externalReference, ConstructionType type, UUID location, int parallelWorkers, int requiredWorkPoints) {
        return create(externalReference, type, location, parallelWorkers, requiredWorkPoints, null);
    }

    public Construction create(UUID externalReference, ConstructionType type, UUID location, int parallelWorkers, int requiredWorkPoints, String data) {
        return Construction.builder()
            .constructionId(idGenerator.randomUuid())
            .externalReference(externalReference)
            .constructionType(type)
            .location(location)
            .parallelWorkers(parallelWorkers)
            .requiredWorkPoints(requiredWorkPoints)
            .currentWorkPoints(0)
            .priority(GameConstants.DEFAULT_PRIORITY)
            .data(data)
            .build();
    }
}
