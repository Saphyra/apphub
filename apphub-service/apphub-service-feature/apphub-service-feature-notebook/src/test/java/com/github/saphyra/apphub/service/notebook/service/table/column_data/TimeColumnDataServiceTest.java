package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class TimeColumnDataServiceTest {
    @InjectMocks
    private TimeColumnDataService underTest;

    @Test
    void validateData_empty() {
        underTest.validateData("");
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "time", "must not be null");
    }

    @Test
    void validateData_parseError() {
        Throwable ex = catchThrowable(() -> underTest.validateData("asd"));

        ExceptionValidator.validateInvalidParam(ex, "time", "failed to parse");
    }

    @Test
    void validateData() {
        underTest.validateData("23:23:45");
    }
}