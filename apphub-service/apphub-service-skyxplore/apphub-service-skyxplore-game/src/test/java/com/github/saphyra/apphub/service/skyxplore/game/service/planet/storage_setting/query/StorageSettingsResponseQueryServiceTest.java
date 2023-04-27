package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingToApiModelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageSettingsResponseQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String AVAILABLE_RESOURCE_ID = "available-resource-id";

    @Mock
    private GameDao gameDao;

    @Mock
    private AvailableResourcesMapper availableResourcesMapper;

    @Mock
    private StorageSettingToApiModelMapper storageSettingToApiModelMapper;

    @InjectMocks
    private StorageSettingsResponseQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSettingApiModel storageSettingModel;

    @Mock
    private StorageSetting storageSetting;

    @Test
    public void getStorageSettings() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getStorageSettings()).willReturn(storageSettings);
        given(storageSettings.getByLocation(PLANET_ID)).willReturn(List.of(storageSetting));

        given(storageSettingToApiModelMapper.convert(List.of(storageSetting))).willReturn(Arrays.asList(storageSettingModel));
        given(availableResourcesMapper.getAvailableResources(List.of(storageSetting))).willReturn(Arrays.asList(AVAILABLE_RESOURCE_ID));

        StorageSettingsResponse result = underTest.getStorageSettings(USER_ID, PLANET_ID);

        assertThat(result.getCurrentSettings()).containsExactly(storageSettingModel);
        assertThat(result.getAvailableResources()).containsExactly(AVAILABLE_RESOURCE_ID);
    }
}