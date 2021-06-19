package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class ColumnNameValidatorTest {
    @InjectMocks
    private ColumnNameValidator underTest;

    @Test
    public void blankColumnName() {
        Throwable ex = catchThrowable(() -> underTest.validate(" "));

        ExceptionValidator.validateInvalidParam(ex, "columnName", "must not be null or blank");
    }

    @Test
    public void valid() {
        underTest.validate("asd");

        //No exception
    }
}