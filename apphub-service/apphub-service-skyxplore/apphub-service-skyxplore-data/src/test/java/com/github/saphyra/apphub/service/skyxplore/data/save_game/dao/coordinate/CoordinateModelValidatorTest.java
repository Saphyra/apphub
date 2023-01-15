package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
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
public class CoordinateModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private CoordinateModelValidator underTest;

    @Mock
    private CoordinateModel model;

    @BeforeEach
    public void setUp() {
        given(model.getReferenceId()).willReturn(UUID.randomUUID());
        given(model.getCoordinate()).willReturn(new Coordinate(0d, 0d));
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
    public void nullCoordinate() {
        given(model.getCoordinate()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "coordinate", "must not be null");
    }

    @Test
    public void nullX() {
        given(model.getCoordinate()).willReturn(new Coordinate(null, 0d));

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "coordinate.x", "must not be null");
    }

    @Test
    public void nullY() {
        given(model.getCoordinate()).willReturn(new Coordinate(0d, null));

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "coordinate.y", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}