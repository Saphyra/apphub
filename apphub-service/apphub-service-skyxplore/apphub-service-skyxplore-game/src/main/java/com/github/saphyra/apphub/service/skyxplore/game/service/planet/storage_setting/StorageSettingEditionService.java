package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StorageSettingToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingEditionService {
    private final GameDao gameDao;
    private final StorageSettingsModelValidator storageSettingsModelValidator;
    private final StorageSettingToModelConverter storageSettingToModelConverter;
    private final GameDataProxy gameDataProxy;

    public void edit(UUID userId, UUID planetId, StorageSettingApiModel request) {
        storageSettingsModelValidator.validate(request);

        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);

        StorageSetting storageSetting = planet.getStorageDetails()
            .getStorageSettings()
            .findByStorageSettingId(request.getStorageSettingId())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "StorageSetting not found with id " + request.getStorageSettingId()));

        storageSetting.setPriority(request.getPriority());
        storageSetting.setBatchSize(request.getBatchSize());

        storageSetting.setTargetAmount(request.getTargetAmount());

        StorageSettingModel model = storageSettingToModelConverter.convert(storageSetting, game.getGameId());
        gameDataProxy.saveItem(model);
    }
}
