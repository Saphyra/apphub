package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class EmailValidatorTest {
    private static final String EMAIL = "asd@asd.asd";

    @InjectMocks
    private EmailValidator underTest;

    @Mock
    private User user;

    @Test
    public void nullEmail() {
        Throwable ex = catchThrowable(() -> underTest.validateEmail(null));

        ExceptionValidator.validateInvalidParam(ex, "email", "must not be null");
    }

    @Test
    public void invalidEmail() {
        Throwable ex = catchThrowable(() -> underTest.validateEmail("asd"));

        ExceptionValidator.validateInvalidParam(ex, "email", "invalid format");
    }

    @Test
    public void valid() {
        underTest.validateEmail(EMAIL);
    }
}