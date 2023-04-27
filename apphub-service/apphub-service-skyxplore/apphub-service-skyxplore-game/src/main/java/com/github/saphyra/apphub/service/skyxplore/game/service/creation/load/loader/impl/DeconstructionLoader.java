package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;

import java.util.List;

public class DeconstructionLoader extends AutoLoader<DeconstructionModel, Deconstruction> {
    public DeconstructionLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.DECONSTRUCTION;
    }

    @Override
    protected Class<DeconstructionModel[]> getArrayClass() {
        return DeconstructionModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Deconstruction> items) {
        gameData.getDeconstructions()
            .addAll(items);
    }

    @Override
    protected Deconstruction convert(DeconstructionModel model) {
        return Deconstruction.builder()
            .deconstructionId(model.getId())
            .externalReference(model.getExternalReference())
            .location(model.getLocation())
            .currentWorkPoints(model.getCurrentWorkPoints())
            .priority(model.getPriority())
            .build();
    }
}
