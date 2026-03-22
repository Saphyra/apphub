package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class DateColumnDataServiceTest {
    @InjectMocks
    private DateColumnDataService underTest;

    @Test
    void validateData_empty() {
        underTest.validateData("");
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "date", "must not be null");
    }

    @Test
    void validateData_parseError() {
        Throwable ex = catchThrowable(() -> underTest.validateData("asd"));

        ExceptionValidator.validateInvalidParam(ex, "date", "failed to parse");
    }

    @Test
    void validateData() {
        underTest.validateData("2023-04-05");
    }
}