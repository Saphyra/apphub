package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
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
public class CitizenModelValidatorTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final String NAME = "name";
    private static final Integer MORALE = 234;
    private static final Integer SATIETY = 3223;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private CitizenModelValidator underTest;

    @Mock
    private CitizenModel model;

    @BeforeEach
    public void setUp() {
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getLocationType()).willReturn(LOCATION_TYPE);
        given(model.getName()).willReturn(NAME);
        given(model.getMorale()).willReturn(MORALE);
        given(model.getSatiety()).willReturn(SATIETY);
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
    public void nullName() {
        given(model.getName()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "name", "must not be null");
    }

    @Test
    public void nullMorale() {
        given(model.getMorale()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "morale", "must not be null");
    }

    @Test
    public void nullSatiety() {
        given(model.getSatiety()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "satiety", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}