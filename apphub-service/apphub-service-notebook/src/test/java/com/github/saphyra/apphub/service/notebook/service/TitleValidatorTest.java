package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.*;

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

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("title")).isEqualTo("must not be null or blank");
    }
}