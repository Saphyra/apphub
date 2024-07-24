package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.service.skyxplore.data.setting.SettingProperties;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SettingValidatorTest {
    private static final int MAX_DATA_SIZE = 3;

    @Mock
    private SettingProperties settingProperties;

    @InjectMocks
    private SettingValidator underTest;

    @Test
    void nullType() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(SettingModel.builder().build()), "type", "must not be null");
    }

    @Test
    void dataTooLarge() {
        SettingModel model = SettingModel.builder()
            .type(SettingType.POPULATION_ORDER)
            .data(Stream.generate(() -> "a").limit(MAX_DATA_SIZE + 1).collect(Collectors.joining()))
            .build();

        given(settingProperties.getMaxDataSize()).willReturn(MAX_DATA_SIZE);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(model), "data", "too long");
    }

    @Test
    void valid() {
        SettingModel model = SettingModel.builder()
            .type(SettingType.POPULATION_ORDER)
            .data(Stream.generate(() -> "a").limit(MAX_DATA_SIZE).collect(Collectors.joining()))
            .build();

        given(settingProperties.getMaxDataSize()).willReturn(MAX_DATA_SIZE);

        underTest.validate(model);
    }
}