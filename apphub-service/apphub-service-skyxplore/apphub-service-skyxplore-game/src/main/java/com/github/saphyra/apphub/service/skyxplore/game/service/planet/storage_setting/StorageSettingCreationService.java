package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettingConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
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
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @SneakyThrows
    public List<StorageSettingApiModel> createStorageSetting(UUID userId, UUID planetId, StorageSettingApiModel request) {
        Game game = gameDao.findByUserIdValidated(userId);

        storageSettingsModelValidator.validate(game.getData(), planetId, request);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(planetId).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot create StorageSetting on planet " + planetId);
        }

        StorageSetting storageSetting = storageSettingFactory.create(request, planetId);
        log.debug("StorageSetting created: {}", storageSetting);

        return game.getEventLoop()
            .processWithResponse(() -> {
                game.getData()
                    .getStorageSettings()
                    .add(storageSetting);

                game.getProgressDiff()
                    .save(storageSettingConverter.toModel(game.getGameId(), storageSetting));

                return storageSettingsResponseQueryService.getStorageSettings(userId, planetId);
            })
            .get()
            .getOrThrow();
    }
}
