package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SystemConnectionModelValidatorTest {
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private SystemConnectionModelValidator underTest;

    @Mock
    private SystemConnectionModel model;

    @Mock
    private Line line;

    @Before
    public void setUp() {
        given(model.getLine()).willReturn(line);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullLine() {
        given(model.getLine()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "line", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}