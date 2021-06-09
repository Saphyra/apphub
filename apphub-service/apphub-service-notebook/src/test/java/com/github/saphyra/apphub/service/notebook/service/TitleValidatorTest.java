package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class TitleValidatorTest {
    @InjectMocks
    private TitleValidator underTest;

    @Test
    public void valid() {
        underTest.validate("title");

        //No exception thrown
    }

    @Test
    public void invalid() {
        Throwable ex = catchThrowable(() -> underTest.validate(" "));

        ExceptionValidator.validateInvalidParam(ex, "title", "must not be null or blank");
    }
}