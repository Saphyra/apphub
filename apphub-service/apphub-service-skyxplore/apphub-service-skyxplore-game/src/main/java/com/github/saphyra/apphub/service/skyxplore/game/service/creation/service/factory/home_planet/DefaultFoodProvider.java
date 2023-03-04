package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DefaultFoodProvider {
    private final StorageSettingFactory storageSettingFactory;
    private final StoredResourceFactory storedResourceFactory;
    private final GameProperties gameProperties;

    void setDefaultFoodSettings(Planet planet) {
        int targetAmount = gameProperties.getPlanet()
            .getDefaultRawFoodAmount();

        StorageSetting storageSetting = storageSettingFactory.create(
            GameConstants.DATA_ID_RAW_FOOD,
            planet.getPlanetId(),
            LocationType.PLANET,
            targetAmount,
            GameConstants.DEFAULT_PRIORITY,
            targetAmount
        );

        planet.getStorageDetails()
            .getStorageSettings()
            .add(storageSetting);

        StoredResource storedResource = storedResourceFactory.create(
            planet.getPlanetId(),
            LocationType.PLANET,
            GameConstants.DATA_ID_RAW_FOOD,
            targetAmount
        );

        planet.getStorageDetails()
            .getStoredResources()
            .add(storedResource);
    }
}
