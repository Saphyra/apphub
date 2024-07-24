package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingDeletionService {
    private final GameDao gameDao;
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @SneakyThrows
    public List<StorageSettingApiModel> deleteStorageSetting(UUID userId, UUID storageSettingId) {
        Game game = gameDao.findByUserIdValidated(userId);

        StorageSetting storageSetting = game.getData()
            .getStorageSettings()
            .findByStorageSettingIdValidated(storageSettingId);

        if (!userId.equals(game.getData().getPlanets().findByIdValidated(storageSetting.getLocation()).getOwner())) {
            throw ExceptionFactory.forbiddenOperation(userId + " cannot delete StorageSetting " + storageSettingId);
        }

        return game.getEventLoop()
            .processWithResponse(
                () -> {
                    game.getData()
                        .getProcesses()
                        .findByExternalReferenceAndType(storageSettingId, ProcessType.STORAGE_SETTING)
                        .ifPresent(Process::cleanup);

                    game.getData()
                        .getStorageSettings()
                        .remove(storageSetting);
                    game.getProgressDiff()
                        .delete(storageSettingId, GameItemType.STORAGE_SETTING);

                    return storageSettingsResponseQueryService.getStorageSettings(userId, storageSetting.getLocation());
                })
            .get()
            .getOrThrow();
    }
}
