package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StorageSettingToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingEditionService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final StorageSettingToModelConverter storageSettingToModelConverter;
    private final StorageSettingToApiModelMapper storageSettingToApiModelMapper;
    private final SyncCacheFactory syncCacheFactory;

    @SneakyThrows
    public StorageSettingApiModel edit(UUID userId, UUID planetId, StorageSettingApiModel request) {
        storageSettingsModelValidator.validate(request);

        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);

        SyncCache syncCache = syncCacheFactory.create();

        return game.getEventLoop()
            .processWithResponse(() -> {
                    StorageSetting storageSetting = planet.getStorageDetails()
                        .getStorageSettings()
                        .findByStorageSettingIdValidated(request.getStorageSettingId());

                    storageSetting.setPriority(request.getPriority());
                    storageSetting.setBatchSize(request.getBatchSize());
                    storageSetting.setTargetAmount(request.getTargetAmount());

                    syncCache.saveGameItem(storageSettingToModelConverter.convert(storageSetting, game.getGameId()));

                    return storageSettingToApiModelMapper.convert(storageSetting);
                },
                syncCache
            )
            .get()
            .getOrThrow();
    }
}
