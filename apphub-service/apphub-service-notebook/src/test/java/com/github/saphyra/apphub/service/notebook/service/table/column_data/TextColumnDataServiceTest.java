package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class TextColumnDataServiceTest {
    @InjectMocks
    private TextColumnDataService underTest;

    @Test
    void validateData_nullValue() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "text", "must not be null");
    }

    @Test
    void validateData() {
        underTest.validateData("");
    }
}