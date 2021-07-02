package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BuildingModelValidatorTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 46;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private BuildingModelValidator underTest;

    @Mock
    private BuildingModel model;

    @Before
    public void setUp() {
        given(model.getSurfaceId()).willReturn(SURFACE_ID);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getLevel()).willReturn(LEVEL);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullSurfaceId() {
        given(model.getSurfaceId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "surfaceId", "must not be null");
    }

    @Test
    public void nullDataId() {
        given(model.getDataId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "must not be null");
    }

    @Test
    public void nullLevel() {
        given(model.getLevel()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "level", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}