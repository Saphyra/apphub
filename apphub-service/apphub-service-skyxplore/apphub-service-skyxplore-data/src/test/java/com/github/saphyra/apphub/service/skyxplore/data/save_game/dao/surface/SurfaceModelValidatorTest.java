package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
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
public class SurfaceModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private SurfaceModelValidator underTest;

    @Mock
    private SurfaceModel model;

    @BeforeEach
    public void setUp() {
        given(model.getPlanetId()).willReturn(UUID.randomUUID());
        given(model.getSurfaceType()).willReturn("asd");
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullPlanetId() {
        given(model.getPlanetId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "planetId", "must not be null");
    }

    @Test
    public void nullSurfaceType() {
        given(model.getSurfaceType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "surfaceType", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}