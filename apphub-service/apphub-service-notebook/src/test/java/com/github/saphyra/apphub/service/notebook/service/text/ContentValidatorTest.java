package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
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