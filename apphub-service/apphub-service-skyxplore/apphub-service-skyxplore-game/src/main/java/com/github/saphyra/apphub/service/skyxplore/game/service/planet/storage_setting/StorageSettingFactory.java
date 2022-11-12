package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StorageSettingFactory {
    private final IdGenerator idGenerator;

    StorageSetting create(StorageSettingApiModel request, UUID location, LocationType locationType) {
        return StorageSetting.builder()
            .storageSettingId(idGenerator.randomUuid())
            .dataId(request.getDataId())
            .location(location)
            .locationType(locationType)
            .targetAmount(request.getTargetAmount())
            .priority(request.getPriority())
            .batchSize(request.getBatchSize())
            .build();
    }
}
