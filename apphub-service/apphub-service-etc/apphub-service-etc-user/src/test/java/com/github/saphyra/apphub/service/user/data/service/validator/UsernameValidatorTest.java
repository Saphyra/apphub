package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class UsernameValidatorTest {
    private static final String USERNAME = "username";

    @InjectMocks
    private UsernameValidator underTest;

    @Mock
    private User user;

    @Test
    public void nullUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername(null));

        ExceptionValidator.validateInvalidParam(ex, "username", "must not be null");
    }

    @Test
    public void tooShortUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername("aa"));

        ExceptionValidator.validateInvalidParam(ex, "username", "too short");
    }

    @Test
    public void tooLongUsername() {
        Throwable ex = catchThrowable(() -> underTest.validateUsername(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "username", "too long");
    }

    @Test
    public void valid() {
        underTest.validateUsername(USERNAME);
    }
}