package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoordinateLoader extends AutoLoader<CoordinateModel, ReferredCoordinate> {
    public CoordinateLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.COORDINATE;
    }

    @Override
    protected Class<CoordinateModel[]> getArrayClass() {
        return CoordinateModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<ReferredCoordinate> items) {
        gameData.getCoordinates()
            .addAll(items);
    }

    @Override
    protected ReferredCoordinate convert(CoordinateModel coordinateModel) {
        return ReferredCoordinate.builder()
            .referredCoordinateId(coordinateModel.getId())
            .referenceId(coordinateModel.getReferenceId())
            .coordinate(coordinateModel.getCoordinate())
            .build();
    }
}
