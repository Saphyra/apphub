package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RangeColumnDataServiceTest {
    private static final Object DATA = "data";
    private static final String STRINGIFIED = "stringified";
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private RangeColumnDataService underTest;

    @Mock
    private Content content;

    @Mock
    private Range range;

    @Test
    void stringifyContent() {
        given(objectMapperWrapper.writeValueAsString(DATA)).willReturn(STRINGIFIED);

        assertThat(underTest.stringifyContent(DATA)).isEqualTo(STRINGIFIED);
    }

    @Test
    void getData() {
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(STRINGIFIED);
        given(objectMapperWrapper.readValue(STRINGIFIED, Range.class)).willReturn(range);

        assertThat(underTest.getData(COLUMN_ID)).isEqualTo(range);
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "range", "must not be null");
    }

    @Test
    void validateData_parseError() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willThrow(new RuntimeException());

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range", "failed to parse");
    }

    @Test
    void validateData_nullValue() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getValue()).willReturn(null);
        given(range.getStep()).willReturn(2d);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.value", "must not be null");
    }

    @Test
    void validateData_nullStep() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getStep()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.step", "must not be null");
    }

    @Test
    void validateData_stepTooLow() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getStep()).willReturn(0d);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.step", "too low");
    }

    @Test
    void validateData_nullMin() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getStep()).willReturn(7d);
        given(range.getMin()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.min", "must not be null");
    }

    @Test
    void validateData_nullMax() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getStep()).willReturn(7d);
        given(range.getMin()).willReturn(23d);
        given(range.getMax()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.max", "must not be null");
    }

    @Test
    void validateData_maxTooLow() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getStep()).willReturn(7d);
        given(range.getMin()).willReturn(23d);
        given(range.getMax()).willReturn(22d);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.max", "too low");
    }

    @Test
    void validateData_valueTooLow() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getValue()).willReturn(11D);
        given(range.getStep()).willReturn(7d);
        given(range.getMin()).willReturn(23d);
        given(range.getMax()).willReturn(25d);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.value", "too low");
    }

    @Test
    void validateData_valueTooHigh() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getValue()).willReturn(111D);
        given(range.getStep()).willReturn(7d);
        given(range.getMin()).willReturn(23d);
        given(range.getMax()).willReturn(25d);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.value", "too high");
    }

    @Test
    void validateData() {
        given(objectMapperWrapper.convertValue(DATA, Range.class)).willReturn(range);
        given(range.getValue()).willReturn(24d);
        given(range.getStep()).willReturn(7d);
        given(range.getMin()).willReturn(23d);
        given(range.getMax()).willReturn(25d);

        underTest.validateData(DATA);
    }
}