package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingCreationService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final StorageSettingFactory storageSettingFactory;
    private final StorageSettingConverter storageSettingConverter;
    private final SyncCacheFactory syncCacheFactory;
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @SneakyThrows
    public List<StorageSettingApiModel> createStorageSetting(UUID userId, UUID planetId, StorageSettingApiModel request) {
        Game game = gameDao.findByUserIdValidated(userId);

        storageSettingsModelValidator.validate(game.getData(), planetId, request);

        StorageSetting storageSetting = storageSettingFactory.create(request, planetId);
        log.debug("StorageSetting created: {}", storageSetting);

        SyncCache syncCache = syncCacheFactory.create();

        return game.getEventLoop()
            .processWithResponse(() -> {
                    game.getData()
                        .getStorageSettings()
                        .add(storageSetting);

                    syncCache.saveGameItem(storageSettingConverter.toModel(game.getGameId(), storageSetting));

                    return storageSettingsResponseQueryService.getStorageSettings(userId, planetId);
                },
                syncCache
            )
            .get()
            .getOrThrow();
    }
}
