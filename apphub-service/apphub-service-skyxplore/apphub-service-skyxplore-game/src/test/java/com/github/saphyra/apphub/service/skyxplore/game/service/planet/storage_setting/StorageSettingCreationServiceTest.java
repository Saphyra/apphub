package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer CURRENT_AMOUNT = 4132413;
    private static final Integer TARGET_AMOUNT = 23423;
    private static final Integer FREE_CAPACITY = 32423;
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageSettingsModelValidator storageSettingsModelValidator;

    @Mock
    private FreeStorageQueryService freeStorageQueryService;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private StorageSettingFactory storageSettingFactory;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @InjectMocks
    private StorageSettingCreationService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private ResourceData resourceData;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorages reservedStorages;

    @Test
    public void createStorageSetting() {
        StorageSettingModel model = StorageSettingModel.builder()
            .dataId(DATA_ID)
            .targetAmount(TARGET_AMOUNT)
            .build();

        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);

        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getStorageType()).willReturn(StorageType.BULK);

        given(freeStorageQueryService.getUsableStoredResourceAmount(DATA_ID, planet)).willReturn(CURRENT_AMOUNT);
        given(freeStorageQueryService.getFreeStorage(planet, StorageType.BULK)).willReturn(FREE_CAPACITY);

        given(storageSettingFactory.create(model, TARGET_AMOUNT - CURRENT_AMOUNT, PLANET_ID, LocationType.PLANET)).willReturn(storageSetting);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);

        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);
        given(reservedStorageFactory.create(STORAGE_SETTING_ID, DATA_ID, TARGET_AMOUNT - CURRENT_AMOUNT)).willReturn(reservedStorage);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);

        underTest.createStorageSetting(USER_ID, PLANET_ID, model);

        verify(storageSettingsModelValidator).validate(model, planet);
        verify(storageSettings).add(storageSetting);
        verify(reservedStorages).add(reservedStorage);
    }
}