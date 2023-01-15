package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlayerModelValidatorTest {
    private static final String USERNAME = "username";
    private static final UUID USER_ID = UUID.randomUUID();
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private PlayerModelValidator underTest;

    @Mock
    private PlayerModel model;

    @AfterEach
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullUserId() {
        given(model.getUserId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "userId", "must not be null");
    }

    @Test
    public void nullUsername() {
        given(model.getUserId()).willReturn(USER_ID);
        given(model.getUsername()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "username", "must not be null");
    }

    @Test
    public void nullAi() {
        given(model.getUserId()).willReturn(USER_ID);
        given(model.getUsername()).willReturn(USERNAME);
        given(model.getAi()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "ai", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getUserId()).willReturn(USER_ID);
        given(model.getUsername()).willReturn(USERNAME);
        given(model.getAi()).willReturn(true);

        underTest.validate(model);
    }
}