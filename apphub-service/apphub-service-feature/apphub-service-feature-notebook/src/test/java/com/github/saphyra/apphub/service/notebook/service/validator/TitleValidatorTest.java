package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class TitleValidatorTest {
    @InjectMocks
    private TitleValidator underTest;

    @Test
    void nullTitle() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "title", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void blankTitle() {
        Throwable ex = catchThrowable(() -> underTest.validate("  "));

        ExceptionValidator.validateInvalidParam(ex, "title", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void valid() {
        underTest.validate("title");
    }
}

