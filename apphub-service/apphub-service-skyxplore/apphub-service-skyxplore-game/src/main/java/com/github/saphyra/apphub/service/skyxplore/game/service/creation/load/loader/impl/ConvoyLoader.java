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
//TODO unit test
class ConvoyLoader extends AutoLoader<ConvoyModel, Convoy> {
    ConvoyLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
    }

    @Override
    protected Class<ConvoyModel[]> getArrayClass() {
    }

    @Override
    protected void addToGameData(GameData gameData, List<Convoy> items) {

    }

    @Override
    protected Convoy convert(ConvoyModel convoyModel) {
    }
}
