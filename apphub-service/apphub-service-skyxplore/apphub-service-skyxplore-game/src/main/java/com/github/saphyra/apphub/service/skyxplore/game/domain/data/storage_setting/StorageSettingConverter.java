package com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingConverter implements GameDataToModelConverter {
    public List<StorageSettingModel> toModel(UUID gameId, Collection<StorageSetting> storageSettings) {
        return storageSettings.stream()
            .map(storageSetting -> toModel(gameId, storageSetting))
            .collect(Collectors.toList());
    }

    public StorageSettingModel toModel(UUID gameId, StorageSetting storageSetting) {
        StorageSettingModel model = new StorageSettingModel();
        model.setId(storageSetting.getStorageSettingId());
        model.setGameId(gameId);
        model.setType(GameItemType.STORAGE_SETTING);
        model.setLocation(storageSetting.getLocation());
        model.setDataId(storageSetting.getDataId());
        model.setTargetAmount(storageSetting.getTargetAmount());
        model.setPriority(storageSetting.getPriority());
        return model;
    }

    @Override
    public List<StorageSettingModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getStorageSettings());
    }
}
