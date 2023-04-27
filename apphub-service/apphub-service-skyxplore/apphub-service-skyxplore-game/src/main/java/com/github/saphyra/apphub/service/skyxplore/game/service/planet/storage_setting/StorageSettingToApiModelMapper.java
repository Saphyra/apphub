package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingToApiModelMapper {
    public List<StorageSettingApiModel> convert(List<StorageSetting> storageSettings) {
        return storageSettings.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public StorageSettingApiModel convert(StorageSetting storageSetting) {
        return StorageSettingApiModel.builder()
            .storageSettingId(storageSetting.getStorageSettingId())
            .dataId(storageSetting.getDataId())
            .targetAmount(storageSetting.getTargetAmount())
            .batchSize(storageSetting.getBatchSize())
            .priority(storageSetting.getPriority())
            .build();
    }
}
