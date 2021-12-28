package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StorageSettingToModelConverter;
import lombok.RequiredArgsConstructor;
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
    private final GameDataProxy gameDataProxy;

    public void createStorageSetting(UUID userId, UUID planetId, StorageSettingApiModel request) {
        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);

        storageSettingsModelValidator.validate(request, planet);

        StorageSetting storageSetting = storageSettingFactory.create(request, request.getTargetAmount(), planetId, LocationType.PLANET);
        log.debug("StorageSetting created: {}", storageSetting);

        planet.getStorageDetails()
            .getStorageSettings()
            .add(storageSetting);

        StorageSettingModel model = storageSettingToModelConverter.convert(storageSetting, game.getGameId());
        gameDataProxy.saveItem(model);
    }
}
