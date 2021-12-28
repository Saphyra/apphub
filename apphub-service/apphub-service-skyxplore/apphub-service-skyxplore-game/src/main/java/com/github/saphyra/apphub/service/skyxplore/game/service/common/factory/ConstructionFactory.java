package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionFactory {
    private final IdGenerator idGenerator;

    public Construction create(UUID externalReference, Integer requiredWorkPoints) {
        return create(externalReference, requiredWorkPoints, null);
    }

    public Construction create(UUID externalReference, Integer requiredWorkPoints, String data) {
        return Construction.builder()
            .constructionId(idGenerator.randomUuid())
            .externalReference(externalReference)
            .requiredWorkPoints(requiredWorkPoints)
            .currentWorkPoints(0)
            .priority(GameConstants.DEFAULT_PRIORITY)
            .data(data)
            .build();
    }
}
