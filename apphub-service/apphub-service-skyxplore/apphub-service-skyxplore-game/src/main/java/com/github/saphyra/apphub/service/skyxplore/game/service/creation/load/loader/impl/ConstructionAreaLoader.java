package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ConstructionAreaLoader extends AutoLoader<ConstructionAreaModel, ConstructionArea> {
    public ConstructionAreaLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.CONSTRUCTION_AREA;
    }

    @Override
    protected Class<ConstructionAreaModel[]> getArrayClass() {
        return ConstructionAreaModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<ConstructionArea> items) {
        gameData.getConstructionAreas()
            .addAll(items);
    }

    @Override
    protected ConstructionArea convert(ConstructionAreaModel model) {
        return ConstructionArea.builder()
            .constructionAreaId(model.getId())
            .location(model.getLocation())
            .surfaceId(model.getSurfaceId())
            .dataId(model.getDataId())
            .build();
    }
}
