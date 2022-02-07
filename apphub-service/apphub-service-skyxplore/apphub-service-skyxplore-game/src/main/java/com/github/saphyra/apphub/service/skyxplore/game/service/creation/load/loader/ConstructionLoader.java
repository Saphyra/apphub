package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionLoader {
    private final GameItemLoader gameItemLoader;

    public Construction load(UUID externalReference) {
        return gameItemLoader.loadChildren(externalReference, GameItemType.CONSTRUCTION, ConstructionModel[].class)
            .stream()
            .findFirst()
            .map(this::convert)
            .orElse(null);
    }

    private Construction convert(ConstructionModel model) {
        return Construction.builder()
            .constructionId(model.getId())
            .externalReference(model.getExternalReference())
            .requiredWorkPoints(model.getRequiredWorkPoints())
            .currentWorkPoints(model.getCurrentWorkPoints())
            .priority(model.getPriority())
            .data(model.getData())
            .parallelWorkers(model.getParallelWorkers())
            .build();
    }
}
