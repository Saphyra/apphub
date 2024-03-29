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

    public StorageSetting create(StorageSettingApiModel request, UUID location) {
        return create(request.getDataId(), location, request.getTargetAmount(), request.getPriority());
    }

    public StorageSetting create(String dataId, UUID location, int targetAmount, int priority) {
        return StorageSetting.builder()
            .storageSettingId(idGenerator.randomUuid())
            .dataId(dataId)
            .location(location)
            .targetAmount(targetAmount)
            .priority(priority)
            .build();
    }
}
