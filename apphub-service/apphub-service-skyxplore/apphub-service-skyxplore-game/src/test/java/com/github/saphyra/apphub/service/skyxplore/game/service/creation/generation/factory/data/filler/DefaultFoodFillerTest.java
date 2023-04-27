package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.StorageSettingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.PlanetProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
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
class DefaultFoodFillerTest {
    private static final Integer DEFAULT_FOOD_AMOUNT = 34;
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private StorageSettingFactory storageSettingFactory;

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private DefaultFoodFiller underTest;

    @Mock
    private Planet planet;

    @Mock
    private Planet emptyPlanet;

    @Mock
    private GameData gameData;

    @Mock
    private PlanetProperties planetProperties;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StorageSettings storageSettings;

    @Test
    void fillDefaultFood() {
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(
            new Planets(),
            new BiWrapper<>(UUID.randomUUID(), planet),
            new BiWrapper<>(UUID.randomUUID(), emptyPlanet)
        ));

        given(planet.hasOwner()).willReturn(true);
        given(emptyPlanet.hasOwner()).willReturn(false);

        given(gameProperties.getPlanet()).willReturn(planetProperties);
        given(planetProperties.getDefaultRawFoodAmount()).willReturn(DEFAULT_FOOD_AMOUNT);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(storageSettingFactory.create(GameConstants.DATA_ID_RAW_FOOD, PLANET_ID, DEFAULT_FOOD_AMOUNT, GameConstants.DEFAULT_PRIORITY, DEFAULT_FOOD_AMOUNT)).willReturn(storageSetting);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storedResourceFactory.create(PLANET_ID, GameConstants.DATA_ID_RAW_FOOD, DEFAULT_FOOD_AMOUNT)).willReturn(storedResource);

        underTest.fillDefaultFood(gameData);

        verify(storageSettings).add(storageSetting);
        verify(storedResources).add(storedResource);
    }
}