package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 23423;

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageSettingsModelValidator storageSettingsModelValidator;

    @Mock
    private StorageSettingFactory storageSettingFactory;

    @InjectMocks
    private StorageSettingCreationService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageDetails storageDetails;

    @Test
    public void createStorageSetting() {
        StorageSettingModel model = StorageSettingModel.builder()
            .dataId(DATA_ID)
            .targetAmount(TARGET_AMOUNT)
            .build();

        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);

        given(storageSettingFactory.create(model, TARGET_AMOUNT, PLANET_ID, LocationType.PLANET)).willReturn(storageSetting);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);
        given(planet.getStorageDetails()).willReturn(storageDetails);

        underTest.createStorageSetting(USER_ID, PLANET_ID, model);

        verify(storageSettingsModelValidator).validate(model, planet);
        verify(storageSettings).add(storageSetting);
    }
}