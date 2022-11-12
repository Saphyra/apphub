package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionToResponseConverter {
    public ConstructionResponse convert(Construction construction) {
        return ConstructionResponse.builder()
            .constructionId(construction.getConstructionId())
            .requiredWorkPoints(construction.getRequiredWorkPoints())
            .currentWorkPoints(construction.getCurrentWorkPoints())
            .data(construction.getData())
            .build();
    }
}
