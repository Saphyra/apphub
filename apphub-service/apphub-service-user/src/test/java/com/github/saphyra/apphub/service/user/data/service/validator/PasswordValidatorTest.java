package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class PasswordValidatorTest {
    @InjectMocks
    private PasswordValidator underTest;

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.validatePassword(null));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void tooShortPassword() {
        Throwable ex = catchThrowable(() -> underTest.validatePassword("aaaaa"));

        ExceptionValidator.validateInvalidParam(ex, "password", "too short");
    }

    @Test
    public void tooLongPassword() {
        Throwable ex = catchThrowable(() -> underTest.validatePassword(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "password", "too long");
    }

    @Test
    public void valid() {
        underTest.validatePassword("asdasd");
    }
}