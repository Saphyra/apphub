package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingDeletionService {
    private final GameDao gameDao;
    private final SyncCacheFactory syncCacheFactory;

    @SneakyThrows
    public void deleteStorageSetting(UUID userId, UUID storageSettingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        SyncCache syncCache = syncCacheFactory.create(game);

        StorageSetting storageSetting = game.getData()
            .getStorageSettings()
            .findByStorageSettingIdValidated(storageSettingId);

        game.getEventLoop()
            .process(
                () -> {
                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndTypeValidated(storageSettingId, ProcessType.STORAGE_SETTING)
                        .cleanup(syncCache);

                    game.getData()
                        .getStorageSettings()
                        .remove(storageSetting);
                    syncCache.deleteGameItem(storageSettingId, GameItemType.STORAGE_SETTING);
                },
                syncCache
            )
            .get()
            .getOrThrow();
    }
}
