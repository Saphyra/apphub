package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.data.common.GameProxy;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.Setting;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SettingQueryServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final SettingIdentifier SETTING_IDENTIFIER = SettingIdentifier.builder()
        .type(SettingType.POPULATION_ORDER)
        .location(LOCATION)
        .build();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String STRINGIFIED_DATA = "stringified-data";
    private static final Object DATA = "data";

    @Mock
    private SettingDao settingDao;

    @Mock
    private GameProxy gameProxy;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private SettingQueryService underTest;

    @Mock
    private Setting setting;

    @BeforeEach
    void setUp() {
        given(gameProxy.getGameIdValidated(USER_ID)).willReturn(GAME_ID);
    }

    @Test
    void returnByLocation() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.of(setting));
        given(setting.getType()).willReturn(SettingType.POPULATION_ORDER);
        given(setting.getLocation()).willReturn(LOCATION);
        given(setting.getData()).willReturn(STRINGIFIED_DATA);
        given(objectMapperWrapper.readValue(STRINGIFIED_DATA, Object.class)).willReturn(DATA);

        assertThat(underTest.getSetting(USER_ID, SETTING_IDENTIFIER))
            .returns(SettingType.POPULATION_ORDER, SettingModel::getType)
            .returns(LOCATION, SettingModel::getLocation)
            .returns(DATA, SettingModel::getData);
    }

    @Test
    void returnGlobal() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.empty());
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, null)).willReturn(Optional.of(setting));
        given(setting.getType()).willReturn(SettingType.POPULATION_ORDER);
        given(setting.getLocation()).willReturn(LOCATION);
        given(setting.getData()).willReturn(STRINGIFIED_DATA);
        given(objectMapperWrapper.readValue(STRINGIFIED_DATA, Object.class)).willReturn(DATA);

        assertThat(underTest.getSetting(USER_ID, SETTING_IDENTIFIER))
            .returns(SettingType.POPULATION_ORDER, SettingModel::getType)
            .returns(LOCATION, SettingModel::getLocation)
            .returns(DATA, SettingModel::getData);
    }

    @Test
    void notFound() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.empty());
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, null)).willReturn(Optional.empty());

        assertThat(underTest.getSetting(USER_ID, SETTING_IDENTIFIER)).isNull();
    }
}