package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingToApiModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingsResponseQueryService {
    private final GameDao gameDao;
    private final AvailableResourcesMapper availableResourcesMapper;
    private final StorageSettingToApiModelMapper storageSettingToApiModelMapper;

    public StorageSettingsResponse getStorageSettings(UUID userId, UUID planetId) {
        List<StorageSetting> storageSettings =gameDao.findByUserIdValidated(userId)
            .getData()
            .getStorageSettings()
            .getByLocation(planetId);

        return StorageSettingsResponse.builder()
            .currentSettings(storageSettingToApiModelMapper.convert(storageSettings))
            .availableResources(availableResourcesMapper.getAvailableResources(storageSettings))
            .build();
    }
}
