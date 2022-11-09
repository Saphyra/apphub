package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
        Planet planet = game
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);

        storageSettingsModelValidator.validate(request, planet);

        StorageSetting storageSetting = storageSettingFactory.create(request, planetId, LocationType.PLANET);
        log.debug("StorageSetting created: {}", storageSetting);

        SyncCache syncCache = syncCacheFactory.create();

        return game.getEventLoop()
            .processWithResponse(() -> {
                    planet.getStorageDetails()
                        .getStorageSettings()
                        .add(storageSetting);

                    Process process = storageSettingProcessFactory.create(game, planet, storageSetting);
                    syncCache.saveGameItem(process.toModel());

                    game.getProcesses()
                        .add(process);
                    syncCache.saveGameItem(storageSettingToModelConverter.convert(storageSetting, game.getGameId()));

                    return storageSettingToApiModelMapper.convert(storageSetting);
                },
                syncCache
            )
            .get()
            .getOrThrow();
    }
}
