package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageSettingLoader extends AutoLoader<StorageSettingModel, StorageSetting> {
    public StorageSettingLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.STORAGE_SETTING;
    }

    @Override
    protected Class<StorageSettingModel[]> getArrayClass() {
        return StorageSettingModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<StorageSetting> items) {
        gameData.getStorageSettings()
            .addAll(items);
    }

    @Override
    protected StorageSetting convert(StorageSettingModel model) {
        return StorageSetting.builder()
            .storageSettingId(model.getId())
            .location(model.getLocation())
            .dataId(model.getDataId())
            .targetAmount(model.getTargetAmount())
            .priority(model.getPriority())
            .batchSize(model.getBatchSize())
            .build();
    }
}
