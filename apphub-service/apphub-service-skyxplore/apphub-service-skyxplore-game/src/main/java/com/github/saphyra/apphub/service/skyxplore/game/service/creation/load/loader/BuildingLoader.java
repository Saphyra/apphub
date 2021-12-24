package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingLoader {
    private final GameItemLoader gameItemLoader;
    private final ConstructionLoader constructionLoader;

    Building load(UUID surfaceId) {
        List<BuildingModel> models = gameItemLoader.loadChildren(surfaceId, GameItemType.BUILDING, BuildingModel[].class);
        return models.stream()
            .findFirst()
            .map(this::convert)
            .orElse(null);
    }

    private Building convert(BuildingModel model) {
        return Building.builder()
            .buildingId(model.getId())
            .surfaceId(model.getSurfaceId())
            .dataId(model.getDataId())
            .level(model.getLevel())
            .construction(constructionLoader.load(model.getId()))
            .build();
    }
}
