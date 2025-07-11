package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionFactory {
    private final IdGenerator idGenerator;

    public Construction create(UUID externalReference, UUID location, int requiredWorkPoints) {
        return create(externalReference, location, requiredWorkPoints, null);
    }

    public Construction create(UUID externalReference, UUID location, int requiredWorkPoints, String data) {
        return Construction.builder()
            .constructionId(idGenerator.randomUuid())
            .externalReference(externalReference)
            .location(location)
            .requiredWorkPoints(requiredWorkPoints)
            .priority(GameConstants.DEFAULT_PRIORITY)
            .data(data)
            .build();
    }
}
