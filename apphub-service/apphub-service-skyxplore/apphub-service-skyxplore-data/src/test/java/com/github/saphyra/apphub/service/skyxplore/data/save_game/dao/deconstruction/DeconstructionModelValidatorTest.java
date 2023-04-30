package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
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
class DeconstructionModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private DeconstructionModelValidator underTest;

    @Mock
    private DeconstructionModel model;

    @AfterEach
    void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullExternalReference() {
        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "externalReference", "must not be null");
    }

    @Test
    public void nullCurrentWorkPoints() {
        given(model.getExternalReference()).willReturn(UUID.randomUUID());
        given(model.getCurrentWorkPoints()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "currentWorkPoints", "must not be null");
    }

    @Test
    public void nullPriority() {
        given(model.getExternalReference()).willReturn(UUID.randomUUID());
        given(model.getCurrentWorkPoints()).willReturn(6544);
        given(model.getPriority()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priority", "must not be null");
    }

    @Test
    public void priorityTooLow() {
        given(model.getExternalReference()).willReturn(UUID.randomUUID());
        given(model.getCurrentWorkPoints()).willReturn(6544);
        given(model.getPriority()).willReturn(0);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too low");
    }

    @Test
    public void priorityTooHigh() {
        given(model.getExternalReference()).willReturn(UUID.randomUUID());
        given(model.getCurrentWorkPoints()).willReturn(6544);
        given(model.getPriority()).willReturn(11);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priority", "too high");
    }

    @Test
    public void valid() {
        given(model.getExternalReference()).willReturn(UUID.randomUUID());
        given(model.getCurrentWorkPoints()).willReturn(6544);
        given(model.getPriority()).willReturn(5);

        underTest.validate(model);
    }
}