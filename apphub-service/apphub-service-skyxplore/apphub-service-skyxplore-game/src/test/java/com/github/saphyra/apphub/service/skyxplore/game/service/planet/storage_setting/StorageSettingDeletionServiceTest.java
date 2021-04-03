package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private StorageSettingDeletionService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private ReservedStorages reservedStorages;

    @Test
    public void deleteStorageSetting() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);

        underTest.deleteStorageSetting(USER_ID, PLANET_ID, STORAGE_SETTING_ID);

        verify(storageSettings).deleteByStorageSettingId(STORAGE_SETTING_ID);
        verify(reservedStorages).deleteByExternalReference(STORAGE_SETTING_ID);
    }
}