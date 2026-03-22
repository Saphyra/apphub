package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class ContentValidatorTest {
    @InjectMocks
    private ContentValidator underTest;

    @Test
    public void validate_valid() {
        underTest.validate("content", "c");

        //No exception thrown
    }

    @Test
    public void validate_invalid() {
        Throwable ex = catchThrowable(() -> underTest.validate(null, "content"));

        ExceptionValidator.validateInvalidParam(ex, "content", "must not be null");
    }
}