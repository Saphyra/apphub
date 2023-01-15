package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AllianceModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private AllianceModelValidator underTest;

    @Mock
    private AllianceModel model;

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullName() {
        given(model.getName()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "name", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getName()).willReturn("asd");

        underTest.validate(model);
    }
}