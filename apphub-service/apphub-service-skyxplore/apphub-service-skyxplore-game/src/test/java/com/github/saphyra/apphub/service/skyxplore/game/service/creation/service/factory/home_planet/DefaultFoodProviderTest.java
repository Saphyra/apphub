package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.PlanetProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultFoodProviderTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer TARGET_AMOUNT = 3241;

    @Mock
    private StorageSettingFactory storageSettingFactory;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private DefaultFoodProvider underTest;

    @Mock
    private PlanetProperties planetProperties;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StorageSetting storageSetting;

    @Test
    void setDefaultFoodSettings() {
        given(gameProperties.getPlanet()).willReturn(planetProperties);
        given(planetProperties.getDefaultRawFoodAmount()).willReturn(TARGET_AMOUNT);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);
        given(storageDetails.getStoredResources()).willReturn(storedResources);

        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(storageSettingFactory.create(GameConstants.DATA_ID_RAW_FOOD, PLANET_ID, LocationType.PLANET, TARGET_AMOUNT, GameConstants.DEFAULT_PRIORITY, TARGET_AMOUNT)).willReturn(storageSetting);
        given(storedResourceFactory.create(PLANET_ID, LocationType.PLANET, GameConstants.DATA_ID_RAW_FOOD, TARGET_AMOUNT)).willReturn(storedResource);

        underTest.setDefaultFoodSettings(planet);

        verify(storageSettings).add(storageSetting);
        verify(storedResources).add(storedResource);
    }
}