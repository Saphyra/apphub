package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProcessModelValidatorTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String STRING = "string";

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private ProcessModelValidator underTest;

    @Mock
    private ProcessModel model;

    @Before
    public void setUp() {
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(ID);
        given(model.getLocationType()).willReturn(STRING);
        given(model.getExternalReference()).willReturn(ID);
        given(model.getData()).willReturn(Collections.emptyMap());
    }

    @After
    public void verifyGameItemValidatorCalled() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }

    @Test
    public void nullProcessType() {
        given(model.getProcessType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "processType", "must not be null");
    }

    @Test
    public void nullStatus() {
        given(model.getStatus()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "status", "must not be null");
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

        ExceptionValidator.validateInvalidParam(ex, "locationType", "must not be null");
    }

    @Test
    public void nullExternalReference() {
        given(model.getExternalReference()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "externalReference", "must not be null");
    }

    @Test
    public void nullData() {
        given(model.getData()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "data", "must not be null");
    }
}