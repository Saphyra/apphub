package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingToApiModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingsResponseQueryService {
    private final GameDao gameDao;
    private final StorageSettingToApiModelMapper storageSettingToApiModelMapper;

    public List<StorageSettingApiModel> getStorageSettings(UUID userId, UUID planetId) {
        return gameDao.findByUserIdValidated(userId)
            .getData()
            .getStorageSettings()
            .getByLocation(planetId)
            .stream()
            .map(storageSettingToApiModelMapper::convert)
            .collect(Collectors.toList());
    }
}
