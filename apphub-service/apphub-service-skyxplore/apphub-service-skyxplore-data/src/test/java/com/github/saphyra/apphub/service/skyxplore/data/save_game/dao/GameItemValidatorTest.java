package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameItemValidatorTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @InjectMocks
    private GameItemValidator underTest;

    @Mock
    private GameItem gameItem;

    @Test
    public void validate_nullId() {
        given(gameItem.getId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(gameItem));

        ExceptionValidator.validateInvalidParam(ex, "id", "must not be null");
    }

    @Test
    public void validate_nullGameId() {
        given(gameItem.getId()).willReturn(ID);
        given(gameItem.getGameId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(gameItem));

        ExceptionValidator.validateInvalidParam(ex, "gameId", "must not be null");
    }

    @Test
    public void validate_valid() {
        given(gameItem.getId()).willReturn(ID);
        given(gameItem.getGameId()).willReturn(GAME_ID);

        underTest.validate(gameItem);
    }

    @Test
    public void validateWithoutId_nullGameId() {
        given(gameItem.getGameId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validateWithoutId(gameItem));

        ExceptionValidator.validateInvalidParam(ex, "gameId", "must not be null");
    }

    @Test
    public void validateWithoutId_valid() {
        given(gameItem.getGameId()).willReturn(GAME_ID);

        underTest.validateWithoutId(gameItem);
    }
}