package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class UserQueryValidatorTest {
    @InjectMocks
    private UserQueryValidator underTest;

    @Test
    void nullQuery() {
        Throwable ex = catchThrowable(() -> underTest.validateQuery(null));

        ExceptionValidator.validateInvalidParam(ex, "query", "must not be null");
    }

    @Test
    void tooShortQuery() {
        Throwable ex = catchThrowable(() -> underTest.validateQuery("ab"));

        ExceptionValidator.validateInvalidParam(ex, "query", "too short");
    }

    @Test
    void validQuery() {
        underTest.validateQuery("abc");
    }
}