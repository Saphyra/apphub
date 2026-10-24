package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
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

        ExceptionValidator.validateInvalidParam(ex, "query", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void tooShortQuery() {
        Throwable ex = catchThrowable(() -> underTest.validateQuery("ab"));

        ExceptionValidator.validateInvalidParam(ex, "query", ValidationUtil.TOO_SHORT);
    }

    @Test
    void validQuery() {
        underTest.validateQuery("abc");
    }
}