package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query.StorageSettingsResponseQueryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;

@RunWith(MockitoJUnitRunner.class)
public class SkyXplorePlanetStorageSettingControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();

    @Mock
    private StorageSettingsResponseQueryService storageSettingsResponseQueryService;

    @Mock
    private StorageSettingCreationService storageSettingCreationService;

    @Mock
    private StorageSettingDeletionService storageSettingDeletionService;

    @Mock
    private StorageSettingEditionService storageSettingEditionService;

    @InjectMocks
    private SkyXplorePlanetStorageSettingControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private StorageSettingsResponse storageSettingsResponse;

    @Mock
    private StorageSettingApiModel storageSettingModel;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getStorageSettings() {
        given(storageSettingsResponseQueryService.getStorageSettings(USER_ID, PLANET_ID)).willReturn(storageSettingsResponse);

        StorageSettingsResponse result = underTest.getStorageSettings(PLANET_ID, accessTokenHeader);

        assertThat(result).isEqualTo(storageSettingsResponse);
    }

    @Test
    public void createStorageSetting() {
        underTest.createStorageSetting(storageSettingModel, PLANET_ID, accessTokenHeader);

        verify(storageSettingCreationService).createStorageSetting(USER_ID, PLANET_ID, storageSettingModel);
    }

    @Test
    public void deleteStorageSetting() {
        underTest.deleteStorageSetting(PLANET_ID, STORAGE_SETTING_ID, accessTokenHeader);

        verify(storageSettingDeletionService).deleteStorageSetting(USER_ID, PLANET_ID, STORAGE_SETTING_ID);
    }

    @Test
    public void editStorageSetting() {
        underTest.editStorageSetting(storageSettingModel, PLANET_ID, accessTokenHeader);

        verify(storageSettingEditionService).edit(USER_ID, PLANET_ID, storageSettingModel);
    }
}