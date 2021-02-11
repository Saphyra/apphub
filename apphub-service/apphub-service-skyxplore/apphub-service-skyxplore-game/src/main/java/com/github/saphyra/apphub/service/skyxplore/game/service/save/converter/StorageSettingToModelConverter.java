package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageSettingToModelConverter {
    public List<StorageSettingModel> convert(List<StorageSetting> storageSettings, Game game) {
        return storageSettings.stream()
            .map(storageSetting -> convert(storageSetting, game))
            .collect(Collectors.toList());
    }

    public StorageSettingModel convert(StorageSetting storageSetting, Game game) {
        StorageSettingModel model = new StorageSettingModel();
        model.setId(storageSetting.getStorageSettingsId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.STORAGE_SETTING);
        model.setLocation(storageSetting.getLocation());
        model.setLocationType(storageSetting.getLocationType().name());
        model.setDataId(storageSetting.getDataId());
        model.setTargetAmount(storageSetting.getTargetAmount());
        model.setPriority(storageSetting.getPriority());
        model.setBatchSize(storageSetting.getBatchSize());
        return model;
    }
}
