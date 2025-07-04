package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
            .priority(model.getPriority())
            .build();
    }
}
