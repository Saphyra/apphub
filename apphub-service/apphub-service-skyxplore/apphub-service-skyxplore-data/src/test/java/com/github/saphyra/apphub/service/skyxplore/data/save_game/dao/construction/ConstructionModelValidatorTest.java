package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
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

@RunWith(MockitoJUnitRunner.class)
public class ConstructionModelValidatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final Integer REQUIRED_WORK_POINTS = 356;
    private static final Integer CURRENT_WORK_POINTS = 678;
    private static final Integer PRIORITY = 467;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private ConstructionModelValidator underTest;

    @Mock
    private ConstructionModel model;

    @Before
    public void setUp() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getLocationType()).willReturn(LOCATION_TYPE);
        given(model.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(model.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(model.getPriority()).willReturn(PRIORITY);
    }

    @After
    public void validate() {
        gameItemValidator.validate(model);
    }

    @Test
    public void nullLocation() {
        given(model.getLocation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "location", "must not be null");
    }

    @Test
    public void nullLocationType() {
        given(model.getLocationType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "locationType", "must not be null or blank");
    }

    @Test
    public void nullRequiredWorkPoints() {
        given(model.getRequiredWorkPoints()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "requiredWorkPoints", "must not be null");
    }

    @Test
    public void nullCurrentWorkPoints() {
        given(model.getCurrentWorkPoints()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "currentWorkPoints", "must not be null");
    }

    @Test
    public void nullPriority() {
        given(model.getPriority()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priority", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}