package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConstructionLoader extends AutoLoader<ConstructionModel, Construction> {
    public ConstructionLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.CONSTRUCTION;
    }

    @Override
    protected Class<ConstructionModel[]> getArrayClass() {
        return ConstructionModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Construction> items) {
        gameData.getConstructions()
            .addAll(items);
    }

    @Override
    protected Construction convert(ConstructionModel model) {
        return Construction.builder()
            .constructionId(model.getId())
            .externalReference(model.getExternalReference())
            .constructionType(model.getConstructionType())
            .location(model.getLocation())
            .parallelWorkers(model.getParallelWorkers())
            .requiredWorkPoints(model.getRequiredWorkPoints())
            .data(model.getData())
            .currentWorkPoints(model.getCurrentWorkPoints())
            .priority(model.getPriority())
            .build();
    }
}
