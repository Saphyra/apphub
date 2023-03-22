package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
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
public class StorageSettingToModelConverter {
    public List<StorageSettingModel> convert(UUID gameId, Collection<StorageSetting> storageSettings) {
        return storageSettings.stream()
            .map(storageSetting -> convert(gameId, storageSetting))
            .collect(Collectors.toList());
    }

    public StorageSettingModel convert(UUID gameId, StorageSetting storageSetting) {
        StorageSettingModel model = new StorageSettingModel();
        model.setId(storageSetting.getStorageSettingId());
        model.setGameId(gameId);
        model.setType(GameItemType.STORAGE_SETTING);
        model.setLocation(storageSetting.getLocation());
        model.setDataId(storageSetting.getDataId());
        model.setTargetAmount(storageSetting.getTargetAmount());
        model.setPriority(storageSetting.getPriority());
        model.setBatchSize(storageSetting.getBatchSize());
        return model;
    }
}
