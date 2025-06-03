package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservedStorageLoader extends AutoLoader<ReservedStorageModel, ReservedStorage> {
    public ReservedStorageLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.RESERVED_STORAGE;
    }

    @Override
    protected Class<ReservedStorageModel[]> getArrayClass() {
        return ReservedStorageModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<ReservedStorage> items) {
        gameData.getReservedStorages()
            .addAll(items);
    }

    @Override
    protected ReservedStorage convert(ReservedStorageModel model) {
        return ReservedStorage.builder()
            .reservedStorageId(model.getId())
            .containerId(model.getContainerId())
            .externalReference(model.getExternalReference())
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .build();
    }
}
