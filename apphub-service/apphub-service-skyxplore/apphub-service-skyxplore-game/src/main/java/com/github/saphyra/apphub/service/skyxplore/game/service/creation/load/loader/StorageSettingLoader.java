package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingLoader {
    private final GameItemLoader gameItemLoader;

    StorageSettings load(UUID location) {
        List<StorageSettingModel> models = gameItemLoader.loadChildren(location, GameItemType.STORAGE_SETTING, StorageSettingModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toCollection(StorageSettings::new));
    }

    private StorageSetting convert(StorageSettingModel model) {
        return StorageSetting.builder()
            .storageSettingId(model.getId())
            .location(model.getLocation())
            .locationType(LocationType.valueOf(model.getLocationType()))
            .dataId(model.getDataId())
            .targetAmount(model.getTargetAmount())
            .priority(model.getPriority())
            .batchSize(model.getBatchSize())
            .build();
    }
}
