package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class ColorColumnDataServiceTest {
    @InjectMocks
    private ColorColumnDataService underTest;

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "color", "must not be null");
    }

    @Test
    void validateData_emptyString() {
        underTest.validateData("");
    }

    @Test
    void validateData_differentLength() {
        Throwable ex = catchThrowable(() -> underTest.validateData("#asd"));

        ExceptionValidator.validateInvalidParam(ex, "color", "must be 7 character(s) long");
    }

    @Test
    void validateData_doesNotStartWithHashTag() {
        Throwable ex = catchThrowable(() -> underTest.validateData(":oooooo"));

        ExceptionValidator.validateInvalidParam(ex, "color", "first character is not #");
    }

    @Test
    void validateData_doesNotHexString() {
        Throwable ex = catchThrowable(() -> underTest.validateData("#oooooo"));

        ExceptionValidator.validateInvalidParam(ex, "color", "failed to parse");
    }

    @Test
    void validateData() {
        underTest.validateData("#123456");
    }
}