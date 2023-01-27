package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
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

@ExtendWith(MockitoExtension.class)
public class ConstructionModelValidatorTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 356;
    private static final Integer CURRENT_WORK_POINTS = 678;
    private static final Integer PRIORITY = 467;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private ConstructionModelValidator underTest;

    @Mock
    private ConstructionModel model;


    @AfterEach
    public void validate() {
        gameItemValidator.validate(model);
    }

    @Test
    public void nullExternalReference() {
        given(model.getExternalReference()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "externalReference", "must not be null");
    }

    @Test
    public void nullRequiredWorkPoints() {
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getRequiredWorkPoints()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "requiredWorkPoints", "must not be null");
    }

    @Test
    public void nullCurrentWorkPoints() {
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(model.getCurrentWorkPoints()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "currentWorkPoints", "must not be null");
    }

    @Test
    public void nullPriority() {
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(model.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(model.getPriority()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "priority", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(model.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(model.getPriority()).willReturn(PRIORITY);

        underTest.validate(model);
    }
}