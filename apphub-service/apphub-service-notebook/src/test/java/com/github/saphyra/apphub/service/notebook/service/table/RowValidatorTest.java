package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RowValidatorTest {
    public static final String COLUMN_VALUE = "column-value";

    @Mock
    private ContentValidator contentValidator;

    @InjectMocks
    private RowValidator underTest;

    @Test
    public void columnAmountDifferent() {
        Throwable ex = catchThrowable(() -> underTest.validate(Arrays.asList(COLUMN_VALUE), 2));

        ExceptionValidator.validateInvalidParam(ex, "columns", "amount different");
    }

    @Test
    public void valid() {
        underTest.validate(Arrays.asList(COLUMN_VALUE), 1);

        verify(contentValidator).validate(COLUMN_VALUE, "columnValue");
    }
}