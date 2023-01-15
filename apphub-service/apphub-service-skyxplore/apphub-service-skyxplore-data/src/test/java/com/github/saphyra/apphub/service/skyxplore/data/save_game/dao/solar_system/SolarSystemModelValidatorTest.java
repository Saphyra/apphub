package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SolarSystemModelValidatorTest {
    private static final Integer RADIUS = 234;
    private static final String DEFAULT_NAME = "default-name";

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private SolarSystemModelValidator underTest;

    @Mock
    private SolarSystemModel model;

    @AfterEach
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullRadius() {
        given(model.getRadius()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "radius", "must not be null");
    }

    @Test
    public void nullDefaultName() {
        given(model.getRadius()).willReturn(RADIUS);
        given(model.getDefaultName()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "defaultName", "must not be null");
    }

    @Test
    public void nullCustomNames() {
        given(model.getRadius()).willReturn(RADIUS);
        given(model.getDefaultName()).willReturn(DEFAULT_NAME);
        given(model.getCustomNames()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "customNames", "must not be null");
    }

    @Test
    public void customNamesContainsNull() {
        given(model.getRadius()).willReturn(RADIUS);
        given(model.getDefaultName()).willReturn(DEFAULT_NAME);
        given(model.getCustomNames()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), null));

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "customNames", "must not contain null");
    }

    @Test
    public void valid() {
        given(model.getRadius()).willReturn(RADIUS);
        given(model.getDefaultName()).willReturn(DEFAULT_NAME);
        given(model.getCustomNames()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), "asd"));

        underTest.validate(model);
    }
}