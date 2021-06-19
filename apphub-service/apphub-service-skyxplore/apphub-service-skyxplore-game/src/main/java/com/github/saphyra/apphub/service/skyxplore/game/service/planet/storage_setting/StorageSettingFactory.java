package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingFactory {
    private final IdGenerator idGenerator;

    StorageSetting create(StorageSettingModel request, int targetAmount, UUID location, LocationType locationType) {
        return StorageSetting.builder()
            .storageSettingId(idGenerator.randomUuid())
            .dataId(request.getDataId())
            .location(location)
            .locationType(locationType)
            .targetAmount(targetAmount)
            .priority(request.getPriority())
            .batchSize(request.getBatchSize())
            .build();
    }
}
