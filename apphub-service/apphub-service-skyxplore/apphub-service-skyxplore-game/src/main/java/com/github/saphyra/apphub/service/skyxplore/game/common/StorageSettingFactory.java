package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingFactory {
    private final IdGenerator idGenerator;

    public StorageSetting create(StorageSettingApiModel request, UUID location, LocationType locationType) {
        return create(request.getDataId(), location, locationType, request.getTargetAmount(), request.getPriority(), request.getBatchSize());
    }

    public StorageSetting create(String dataId, UUID location, LocationType locationType, int targetAmount, int priority, int batchSize) {
        return StorageSetting.builder()
            .storageSettingId(idGenerator.randomUuid())
            .dataId(dataId)
            .location(location)
            .locationType(locationType)
            .targetAmount(targetAmount)
            .priority(priority)
            .batchSize(batchSize)
            .build();
    }
}
