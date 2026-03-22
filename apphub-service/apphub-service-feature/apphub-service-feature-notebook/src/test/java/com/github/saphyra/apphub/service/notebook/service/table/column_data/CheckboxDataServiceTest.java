package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class CheckboxDataServiceTest {
    @InjectMocks
    private CheckboxDataService underTest;

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "checked", "must not be null");
    }

    @Test
    void validateData() {
        underTest.validateData(Boolean.TRUE.toString());
    }
}