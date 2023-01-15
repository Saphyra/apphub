package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
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
public class AllocatedResourceModelValidatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 234;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private AllocatedResourceModelValidator underTest;

    @Mock
    private AllocatedResourceModel model;

    @BeforeEach
    public void setUp() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getLocationType()).willReturn(LOCATION_TYPE);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getAmount()).willReturn(AMOUNT);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
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
    public void nullDataId() {
        given(model.getDataId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "dataId", "must not be null");
    }

    @Test
    public void nullAmount() {
        given(model.getAmount()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "amount", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}