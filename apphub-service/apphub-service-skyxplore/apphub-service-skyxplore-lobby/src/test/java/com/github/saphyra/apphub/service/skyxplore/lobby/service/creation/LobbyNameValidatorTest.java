package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class LobbyNameValidatorTest {
    @InjectMocks
    private LobbyNameValidator underTest;

    @Test
    public void nullLobbyName() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("lobbyName", "must not be null");
    }

    @Test
    public void tooShortLobbyName() {
        Throwable ex = catchThrowable(() -> underTest.validate("aa"));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("lobbyName", "too short");
    }

    @Test
    public void tooLongLobbyName() {
        Throwable ex = catchThrowable(() -> underTest.validate(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams()).containsEntry("lobbyName", "too long");
    }

    @Test
    public void valid() {
        underTest.validate("lobby-name");
    }
}