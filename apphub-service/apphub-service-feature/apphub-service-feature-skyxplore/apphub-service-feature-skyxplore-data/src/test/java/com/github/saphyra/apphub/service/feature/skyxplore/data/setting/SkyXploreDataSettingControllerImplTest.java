package com.github.saphyra.apphub.service.feature.skyxplore.data.setting;

import com.github.saphyra.apphub.api.feature.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.feature.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.skyxplore.data.setting.service.CreateOrUpdateSettingService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.setting.service.DeleteSettingService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.setting.service.SettingQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SkyXploreDataSettingControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CreateOrUpdateSettingService createOrUpdateSettingService;

    @Mock
    private SettingQueryService settingQueryService;

    @Mock
    private DeleteSettingService deleteSettingService;

    @InjectMocks
    private SkyXploreDataSettingControllerImpl underTest;

    @Mock
    private SettingModel settingModel;

    @Mock
    private AccessToken accessToken;

    @Mock
    private SettingIdentifier settingIdentifier;

    @BeforeEach
    void setUp() {
        given(accessToken.getUserId()).willReturn(USER_ID);
    }

    @Test
    void createOrUpdateSetting() {
        underTest.createOrUpdateSetting(settingModel, accessToken);

        then(createOrUpdateSettingService).should().createOrUpdate(USER_ID, settingModel);
    }

    @Test
    void getSetting() {
        given(settingQueryService.getSetting(USER_ID, settingIdentifier)).willReturn(settingModel);

        assertThat(underTest.getSetting(settingIdentifier, accessToken)).returns(settingModel, OneParamResponse::getValue);
    }

    @Test
    void deleteSetting() {
        given(settingQueryService.getSetting(USER_ID, settingIdentifier)).willReturn(settingModel);

        assertThat(underTest.deleteSetting(settingIdentifier, accessToken)).returns(settingModel, OneParamResponse::getValue);

        then(deleteSettingService).should().delete(USER_ID, settingIdentifier);
    }
}