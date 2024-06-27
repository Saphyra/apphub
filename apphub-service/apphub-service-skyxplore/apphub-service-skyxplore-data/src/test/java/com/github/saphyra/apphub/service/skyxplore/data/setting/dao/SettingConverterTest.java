package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SettingConverterTest {
    private static final UUID SETTING_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA = "data";
    private static final String SETTING_ID_STRING = "setting-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private SettingConverter underTest;

    @Test
    void convertDomain() {
        Setting domain = Setting.builder()
            .settingId(SETTING_ID)
            .gameId(GAME_ID)
            .userId(USER_ID)
            .type(SettingType.POPULATION_HIDE)
            .location(LOCATION)
            .data(DATA)
            .build();

        given(uuidConverter.convertDomain(SETTING_ID)).willReturn(SETTING_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(SETTING_ID_STRING, SettingEntity::getSettingId)
            .returns(GAME_ID_STRING, SettingEntity::getGameId)
            .returns(USER_ID_STRING, SettingEntity::getUserId)
            .returns(SettingType.POPULATION_HIDE, SettingEntity::getType)
            .returns(LOCATION_STRING, SettingEntity::getLocation)
            .returns(DATA, SettingEntity::getData);
    }

    @Test
    void convertEntity() {
        SettingEntity domain = SettingEntity.builder()
            .settingId(SETTING_ID_STRING)
            .gameId(GAME_ID_STRING)
            .userId(USER_ID_STRING)
            .type(SettingType.POPULATION_HIDE)
            .location(LOCATION_STRING)
            .data(DATA)
            .build();

        given(uuidConverter.convertEntity(SETTING_ID_STRING)).willReturn(SETTING_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        assertThat(underTest.convertEntity(domain))
            .returns(SETTING_ID, Setting::getSettingId)
            .returns(GAME_ID, Setting::getGameId)
            .returns(USER_ID, Setting::getUserId)
            .returns(SettingType.POPULATION_HIDE, Setting::getType)
            .returns(LOCATION, Setting::getLocation)
            .returns(DATA, Setting::getData);
    }
}