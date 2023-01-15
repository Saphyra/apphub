package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
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
public class LineModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private LineModelValidator underTest;

    @Mock
    private LineModel model;

    @BeforeEach
    public void setUp() {
        given(model.getReferenceId()).willReturn(UUID.randomUUID());
        given(model.getA()).willReturn(UUID.randomUUID());
        given(model.getB()).willReturn(UUID.randomUUID());
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullReferenceId() {
        given(model.getReferenceId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "referenceId", "must not be null");
    }

    @Test
    public void nullA() {
        given(model.getA()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "a", "must not be null");
    }

    @Test
    public void nullB() {
        given(model.getB()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "b", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}