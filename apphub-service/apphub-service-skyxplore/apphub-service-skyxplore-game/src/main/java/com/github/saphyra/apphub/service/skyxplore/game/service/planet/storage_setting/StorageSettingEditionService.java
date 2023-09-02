package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCacheFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettingConverter;
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
    private final StorageSettingConverter storageSettingConverter;
    private final StorageSettingToApiModelMapper storageSettingToApiModelMapper;
    private final SyncCacheFactory syncCacheFactory;

    @SneakyThrows
    public StorageSettingApiModel edit(UUID userId, StorageSettingApiModel request) {
        storageSettingsModelValidator.validate(request);

        Game game = gameDao.findByUserIdValidated(userId);

        SyncCache syncCache = syncCacheFactory.create(game);

        return game.getEventLoop()
            .processWithResponse(() -> {
                    StorageSetting storageSetting =game.getData()
                        .getStorageSettings()
                        .findByStorageSettingIdValidated(request.getStorageSettingId());

                    storageSetting.setPriority(request.getPriority());
                    storageSetting.setTargetAmount(request.getTargetAmount());

                    syncCache.saveGameItem(storageSettingConverter.toModel(game.getGameId(), storageSetting));

                    return storageSettingToApiModelMapper.convert(storageSetting);
                },
                syncCache
            )
            .get()
            .getOrThrow();
    }
}
