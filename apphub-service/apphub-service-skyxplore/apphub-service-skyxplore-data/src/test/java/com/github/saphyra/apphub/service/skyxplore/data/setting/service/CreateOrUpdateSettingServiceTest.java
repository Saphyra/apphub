package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.service.skyxplore.data.common.GameProxy;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.Setting;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateOrUpdateSettingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Object DATA = "data";
    private static final String STRINGIFIED_DATA = "stringified-data";

    @Mock
    private SettingValidator settingValidator;

    @Mock
    private GameProxy gameProxy;

    @Mock
    private SettingDao settingDao;

    @Mock
    private SettingFactory settingFactory;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CreateOrUpdateSettingService underTest;

    @Mock
    private SettingModel settingModel;

    @Mock
    private Setting setting;

    @BeforeEach
    void setUp() {
        given(gameProxy.getGameIdValidated(USER_ID)).willReturn(GAME_ID);
        given(settingModel.getType()).willReturn(SettingType.POPULATION_ORDER);
        given(settingModel.getLocation()).willReturn(LOCATION);
        given(settingModel.getData()).willReturn(DATA);
    }

    @AfterEach
    void validate() {
        then(settingValidator).should().validate(settingModel);
        then(setting).should().setData(STRINGIFIED_DATA);
        then(settingDao).should().save(setting);
    }

    @Test
    void create() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.empty());
        given(settingFactory.create(GAME_ID, USER_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(setting);
        given(objectMapper.writeValueAsString(DATA)).willReturn(STRINGIFIED_DATA);

        underTest.createOrUpdate(USER_ID, settingModel);
    }

    @Test
    void update() {
        given(settingDao.findByUserIdAndGameIdAndTypeAndLocation(USER_ID, GAME_ID, SettingType.POPULATION_ORDER, LOCATION)).willReturn(Optional.of(setting));
        given(objectMapper.writeValueAsString(DATA)).willReturn(STRINGIFIED_DATA);

        underTest.createOrUpdate(USER_ID, settingModel);
    }
}