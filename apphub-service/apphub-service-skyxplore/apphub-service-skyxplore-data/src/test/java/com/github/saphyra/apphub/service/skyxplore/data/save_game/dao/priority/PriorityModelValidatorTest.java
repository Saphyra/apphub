package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
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
public class PriorityModelValidatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String PRIORITY_TYPE = "priority-type";
    private static final Integer VALUE = 2354;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private PriorityModelValidator underTest;

    @Mock
    private PriorityModel model;

    @AfterEach
    public void validate() {
        verify(gameItemValidator).validateWithoutId(model);
    }

    @Test
    public void nullLocation() {
        given(model.getLocation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "location", "must not be null");
    }

    @Test
    public void nullPriorityType() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getPriorityType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priorityType", "must not be null");
    }

    @Test
    public void nullValue() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getPriorityType()).willReturn(PRIORITY_TYPE);
        given(model.getValue()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "value", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getPriorityType()).willReturn(PRIORITY_TYPE);
        given(model.getValue()).willReturn(VALUE);

        underTest.validate(model);
    }
}