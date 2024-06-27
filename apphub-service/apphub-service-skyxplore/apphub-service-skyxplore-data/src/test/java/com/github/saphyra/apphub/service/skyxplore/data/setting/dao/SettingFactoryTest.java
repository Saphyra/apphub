package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SettingFactoryTest {
    private static final UUID SETTING_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private SettingFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(SETTING_ID);

        assertThat(underTest.create(GAME_ID, USER_ID, SettingType.POPULATION_HIDE, LOCATION))
            .returns(SETTING_ID, Setting::getSettingId)
            .returns(GAME_ID, Setting::getGameId)
            .returns(USER_ID, Setting::getUserId)
            .returns(SettingType.POPULATION_HIDE, Setting::getType)
            .returns(LOCATION, Setting::getLocation);
    }
}