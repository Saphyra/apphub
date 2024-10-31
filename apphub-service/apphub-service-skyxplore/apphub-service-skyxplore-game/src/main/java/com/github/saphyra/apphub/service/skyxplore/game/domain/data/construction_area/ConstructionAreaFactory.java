package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ConstructionAreaFactory {
    private final IdGenerator idGenerator;

    public ConstructionArea create(UUID location, UUID surfaceId, String dataId) {
        return ConstructionArea.builder()
            .constructionAreaId(idGenerator.randomUuid())
            .location(location)
            .surfaceId(surfaceId)
            .dataId(dataId)
            .build();
    }
}
