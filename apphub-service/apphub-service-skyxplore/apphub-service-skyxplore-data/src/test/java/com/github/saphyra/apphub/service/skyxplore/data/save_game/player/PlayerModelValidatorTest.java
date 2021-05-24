package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlayerModelValidatorTest {
    private static final String USERNAME = "username";
    private static final UUID USER_ID = UUID.randomUUID();
    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private PlayerModelValidator underTest;

    @Mock
    private PlayerModel model;

    @Before
    public void setUp() {
        given(model.getUserId()).willReturn(USER_ID);
        given(model.getUsername()).willReturn(USERNAME);
        given(model.getAi()).willReturn(true);
    }

    @After
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullUserId() {
        given(model.getUserId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("userId", "must not be null");
    }

    @Test
    public void nullUsername() {
        given(model.getUsername()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("username", "must not be null");
    }

    @Test
    public void nullAi() {
        given(model.getAi()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("ai", "must not be null");
    }

    @Test
    public void valid() {
        underTest.validate(model);
    }
}