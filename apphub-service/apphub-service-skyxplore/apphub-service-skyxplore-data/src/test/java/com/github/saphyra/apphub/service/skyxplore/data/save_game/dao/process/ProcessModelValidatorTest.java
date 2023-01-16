package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProcessModelValidatorTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String STRING = "string";

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private ProcessModelValidator underTest;

    @Mock
    private ProcessModel model;

    @AfterEach
    public void verifyGameItemValidatorCalled() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void valid() {
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(ID);
        given(model.getLocationType()).willReturn(STRING);
        given(model.getExternalReference()).willReturn(ID);
        given(model.getData()).willReturn(Collections.emptyMap());

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
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "status", "must not be null");
    }

    @Test
    public void nullLocation() {
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "location", "must not be null");
    }

    @Test
    public void nullLocationType() {
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(ID);
        given(model.getLocationType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "locationType", "must not be null");
    }

    @Test
    public void nullExternalReference() {
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(ID);
        given(model.getLocationType()).willReturn(STRING);
        given(model.getExternalReference()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "externalReference", "must not be null");
    }

    @Test
    public void nullData() {
        given(model.getProcessType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(model.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(model.getLocation()).willReturn(ID);
        given(model.getLocationType()).willReturn(STRING);
        given(model.getExternalReference()).willReturn(ID);
        given(model.getData()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "data", "must not be null");
    }
}