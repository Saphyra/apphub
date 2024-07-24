package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SettingDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private SettingRepository repository;


    @Mock
    private SettingConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private SettingDao underTest;

    @Mock
    private Setting domain;

    @Mock
    private SettingEntity entity;

    @Test
    void findByGameIdAndTypeAndLocation() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        given(repository.findByUserIdAndGameIdAndTypeAndLocation(USER_ID_STRING, GAME_ID_STRING, SettingType.POPULATION_HIDE, LOCATION_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_HIDE, LOCATION)).contains(domain);
    }

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        then(repository).should().deleteByGameId(GAME_ID_STRING);
    }
}