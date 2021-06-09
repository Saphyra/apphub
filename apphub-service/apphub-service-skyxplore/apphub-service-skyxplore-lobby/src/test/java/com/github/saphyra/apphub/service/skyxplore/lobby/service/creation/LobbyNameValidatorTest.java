package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class LobbyNameValidatorTest {
    @InjectMocks
    private LobbyNameValidator underTest;

    @Test
    public void nullLobbyName() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "lobbyName", "must not be null");
    }

    @Test
    public void tooShortLobbyName() {
        Throwable ex = catchThrowable(() -> underTest.validate("aa"));

        ExceptionValidator.validateInvalidParam(ex, "lobbyName", "too short");
    }

    @Test
    public void tooLongLobbyName() {
        Throwable ex = catchThrowable(() -> underTest.validate(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "lobbyName", "too long");
    }

    @Test
    public void valid() {
        underTest.validate("lobby-name");
    }
}