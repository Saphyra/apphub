package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingsResponseQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String AVAILABLE_RESOURCE_ID = "available-resource-id";
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 35124;

    @Mock
    private GameDao gameDao;

    @Mock
    private AvailableResourcesMapper availableResourcesMapper;

    @Mock
    private CurrentSettingsMapper currentSettingsMapper;

    @Mock
    private AvailableStorageMapper availableStorageMapper;

    @InjectMocks
    private StorageSettingsResponseQueryService underTest;

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
    private StorageSettingModel storageSettingModel;

    @Mock
    private Building building;

    @Test
    public void getStorageSettings() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStorageSettings()).willReturn(storageSettings);
        given(planet.getBuildings()).willReturn(Arrays.asList(building));

        given(currentSettingsMapper.convert(storageSettings)).willReturn(Arrays.asList(storageSettingModel));
        given(availableResourcesMapper.getAvailableResources(storageSettings)).willReturn(Arrays.asList(AVAILABLE_RESOURCE_ID));
        given(availableStorageMapper.countAvailableStorage(Arrays.asList(building), storageDetails)).willReturn(CollectionUtils.singleValueMap(DATA_ID, AMOUNT));

        StorageSettingsResponse result = underTest.getStorageSettings(USER_ID, PLANET_ID);

        assertThat(result.getCurrentSettings()).containsExactly(storageSettingModel);
        assertThat(result.getAvailableResources()).containsExactly(AVAILABLE_RESOURCE_ID);
        assertThat(result.getAvailableStorage()).containsEntry(DATA_ID, AMOUNT);
    }
}