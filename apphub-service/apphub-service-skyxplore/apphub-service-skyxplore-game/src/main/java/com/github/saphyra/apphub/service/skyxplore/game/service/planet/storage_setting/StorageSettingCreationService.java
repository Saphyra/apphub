package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.storage_setting.StorageSettingProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StorageSettingToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingCreationService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final StorageSettingFactory storageSettingFactory;
    private final StorageSettingToModelConverter storageSettingToModelConverter;
    private final StorageSettingToApiModelMapper storageSettingToApiModelMapper;
    private final SyncCacheFactory syncCacheFactory;
    private final StorageSettingProcessFactory storageSettingProcessFactory;

    @SneakyThrows
    public StorageSettingApiModel createStorageSetting(UUID userId, UUID planetId, StorageSettingApiModel request) {
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

                    Process process = storageSettingProcessFactory.create(game.getData(), planetId, storageSetting);
                    syncCache.saveGameItem(process.toModel());

                    game.getData()
                        .getProcesses()
                        .add(process);
                    syncCache.saveGameItem(storageSettingToModelConverter.convert(game.getGameId(), storageSetting));

                    return storageSettingToApiModelMapper.convert(storageSetting);
                },
                syncCache
            )
            .get()
            .getOrThrow();
    }
}
