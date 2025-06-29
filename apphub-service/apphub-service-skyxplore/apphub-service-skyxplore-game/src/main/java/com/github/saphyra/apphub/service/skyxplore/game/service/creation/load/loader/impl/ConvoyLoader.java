package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class ConvoyLoader extends AutoLoader<ConvoyModel, Convoy> {
    ConvoyLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.CONVOY;
    }

    @Override
    protected Class<ConvoyModel[]> getArrayClass() {
        return ConvoyModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Convoy> items) {
        gameData.getConvoys()
            .addAll(items);
    }

    @Override
    protected Convoy convert(ConvoyModel model) {
        return Convoy.builder()
            .convoyId(model.getId())
            .resourceDeliveryRequestId(model.getResourceDeliveryRequestId())
            .capacity(model.getCapacity())
            .build();
    }
}
