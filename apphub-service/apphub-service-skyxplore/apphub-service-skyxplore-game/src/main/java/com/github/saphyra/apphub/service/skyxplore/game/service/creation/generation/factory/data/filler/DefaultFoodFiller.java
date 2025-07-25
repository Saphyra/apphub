package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultFoodFiller {
    private final StorageSettingFactory storageSettingFactory;
    private final StoredResourceFactory storedResourceFactory;
    private final GameProperties gameProperties;

    public void fillDefaultFood(GameData gameData) {
        gameData.getPlanets()
            .values()
            .stream()
            .filter(Planet::hasOwner)
            .forEach(planet -> fillDefaultFood(planet, gameData));
    }

    private void fillDefaultFood(Planet planet, GameData gameData) {
        int targetAmount = gameProperties.getPlanet()
            .getDefaultRawFoodAmount();

        StorageSetting storageSetting = storageSettingFactory.create(
            GameConstants.DATA_ID_RAW_FOOD,
            planet.getPlanetId(),
            targetAmount,
            GameConstants.DEFAULT_PRIORITY
        );

        gameData.getStorageSettings()
            .add(storageSetting);

        UUID containerId = gameData.getBuildingModules()
            .getByLocationAndDataId(planet.getPlanetId(), GameConstants.BUILDING_MODULE_HQ_STORAGE)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Planet has no HeadquarterStorage module built."))
            .getBuildingModuleId();

        StoredResource storedResource = storedResourceFactory.create(
            planet.getPlanetId(),
            GameConstants.DATA_ID_RAW_FOOD,
            targetAmount,
            containerId,
            ContainerType.STORAGE,
            null
        );

        gameData.getStoredResources()
            .add(storedResource);
    }
}
