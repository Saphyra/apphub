package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class ContentValidatorTest {
    @InjectMocks
    private ContentValidator underTest;

    @Test
    public void validate_valid() {
        underTest.validate("content");

        //No exception thrown
    }

    @Test
    public void validate_invalid() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("content")).isEqualTo("must not be null");
    }
}