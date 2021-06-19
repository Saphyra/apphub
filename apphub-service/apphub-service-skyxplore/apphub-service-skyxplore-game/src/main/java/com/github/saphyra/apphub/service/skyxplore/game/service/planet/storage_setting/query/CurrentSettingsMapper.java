package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class CurrentSettingsMapper {
    List<StorageSettingModel> convert(List<StorageSetting> storageSettings) {
        return storageSettings.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private StorageSettingModel convert(StorageSetting storageSetting) {
        return StorageSettingModel.builder()
            .storageSettingId(storageSetting.getStorageSettingId())
            .dataId(storageSetting.getDataId())
            .targetAmount(storageSetting.getTargetAmount())
            .batchSize(storageSetting.getBatchSize())
            .priority(storageSetting.getPriority())
            .build();
    }
}
