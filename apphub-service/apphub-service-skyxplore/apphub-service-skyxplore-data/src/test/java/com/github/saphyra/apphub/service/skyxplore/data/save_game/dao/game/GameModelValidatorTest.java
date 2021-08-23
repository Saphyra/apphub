package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameModelValidatorTest {
    private static final String NAME = "name";
    private static final UUID HOST = UUID.randomUUID();
    private static final LocalDateTime LAST_PLAYED = LocalDateTime.now();

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private GameModelValidator underTest;

    @Mock
    private GameModel model;

    @Before
    public void setUp() {
        given(model.getName()).willReturn(NAME);
        given(model.getHost()).willReturn(HOST);
        given(model.getLastPlayed()).willReturn(LAST_PLAYED);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullHost() {
        given(model.getHost()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "host", "must not be null");
    }

    @Test
    public void nullName() {
        given(model.getName()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "name", "must not be null");
    }

    @Test
    public void nullLastPlayed() {
        given(model.getLastPlayed()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "lastPlayed", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }

}