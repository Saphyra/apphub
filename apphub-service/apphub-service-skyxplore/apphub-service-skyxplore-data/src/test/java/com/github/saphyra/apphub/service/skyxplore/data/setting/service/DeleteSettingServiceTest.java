package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.common.GameProxy;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.Setting;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteSettingServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final SettingIdentifier SETTING_IDENTIFIER = SettingIdentifier.builder()
        .location(LOCATION)
        .type(SettingType.POPULATION_ORDER)
        .build();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameProxy gameProxy;

    @Mock
    private SettingDao settingDao;

    @InjectMocks
    private DeleteSettingService underTest;

    @Mock
    private Setting setting;

    @BeforeEach
    void setUp() {
        given(gameProxy.getGameIdValidated(USER_ID)).willReturn(GAME_ID);
    }

    @Test
    void settingNotFound() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.delete(USER_ID, SETTING_IDENTIFIER), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void delete() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.of(setting));

        underTest.delete(USER_ID, SETTING_IDENTIFIER);

        then(settingDao).should().delete(setting);
    }
}