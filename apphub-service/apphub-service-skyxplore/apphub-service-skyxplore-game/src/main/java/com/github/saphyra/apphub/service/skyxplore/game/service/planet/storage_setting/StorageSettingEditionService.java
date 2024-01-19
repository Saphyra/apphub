package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettingConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingEditionService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final StorageSettingConverter storageSettingConverter;
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @SneakyThrows
    public List<StorageSettingApiModel> edit(UUID userId, StorageSettingApiModel request) {
        storageSettingsModelValidator.validate(request);

        Game game = gameDao.findByUserIdValidated(userId);

        return game.getEventLoop()
            .processWithResponse(() -> {
                StorageSetting storageSetting = game.getData()
                    .getStorageSettings()
                    .findByStorageSettingIdValidated(request.getStorageSettingId());

                storageSetting.setPriority(request.getPriority());
                storageSetting.setTargetAmount(request.getTargetAmount());

                game.getProgressDiff()
                    .save(storageSettingConverter.toModel(game.getGameId(), storageSetting));

                return storageSettingsResponseQueryService.getStorageSettings(userId, storageSetting.getLocation());
            })
            .get()
            .getOrThrow();
    }
}
