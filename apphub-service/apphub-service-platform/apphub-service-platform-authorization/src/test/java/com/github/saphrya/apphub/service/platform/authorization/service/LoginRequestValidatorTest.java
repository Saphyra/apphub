package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class LoginRequestValidatorTest {
    @InjectMocks
    private LoginRequestValidator underTest;

    @Test
    void blankUserIdentifier() {
        LoginRequest request = LoginRequest.builder()
            .userIdentifier(" ")
            .password("password")
            .rememberMe(false)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void emptyPassword() {
        LoginRequest request = LoginRequest.builder()
            .userIdentifier("user@example.com")
            .password("")
            .rememberMe(false)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void nullRememberMe() {
        LoginRequest request = LoginRequest.builder()
            .userIdentifier("user@example.com")
            .password("password")
            .rememberMe(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "rememberMe", "must not be null");
    }

    @Test
    void valid() {
        LoginRequest request = LoginRequest.builder()
            .userIdentifier("user@example.com")
            .password("password")
            .rememberMe(true)
            .build();

        underTest.validate(request);
    }
}