package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @AfterEach
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
        given(model.getHost()).willReturn(HOST);
        given(model.getName()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "name", "must not be null");
    }

    @Test
    public void nullLastPlayed() {
        given(model.getName()).willReturn(NAME);
        given(model.getHost()).willReturn(HOST);
        given(model.getLastPlayed()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "lastPlayed", "must not be null");
    }

    @Test
    public void nullMarkedForDeletion() {
        given(model.getName()).willReturn(NAME);
        given(model.getHost()).willReturn(HOST);
        given(model.getLastPlayed()).willReturn(LAST_PLAYED);
        given(model.getMarkedForDeletion()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "markedForDeletion", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getName()).willReturn(NAME);
        given(model.getHost()).willReturn(HOST);
        given(model.getLastPlayed()).willReturn(LAST_PLAYED);
        given(model.getMarkedForDeletion()).willReturn(false);

        underTest.validate(model);
    }

}