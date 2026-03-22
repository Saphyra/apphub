package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NumberColumnDataServiceTest {
    private static final Object DATA = "data";
    private static final String STRINGIFIED = "stringified";
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NumberColumnDataService underTest;

    @Mock
    private Content content;

    @Mock
    private Number number;

    @Test
    void stringifyContent() {
        given(objectMapper.writeValueAsString(DATA)).willReturn(STRINGIFIED);

        assertThat(underTest.stringifyContent(DATA)).isEqualTo(STRINGIFIED);
    }

    @Test
    void getData() {
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(STRINGIFIED);
        given(objectMapper.readValue(STRINGIFIED, Number.class)).willReturn(number);

        assertThat(underTest.getData(COLUMN_ID)).isEqualTo(number);
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "number", "must not be null");
    }

    @Test
    void validateData_parseError() {
        given(objectMapper.convertValue(DATA, Number.class)).willThrow(new RuntimeException());

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number", "failed to parse");
    }

    @Test
    void validateData_nullValue() {
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);
        given(number.getValue()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number.value", "must not be null");
    }

    @Test
    void validateData_nullStep() {
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);
        given(number.getValue()).willReturn(11D);
        given(number.getStep()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number.step", "must not be null");
    }

    @Test
    void validateData_stepTooLow() {
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);
        given(number.getValue()).willReturn(11D);
        given(number.getStep()).willReturn(0d);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number.step", "too low");
    }

    @Test
    void validateData() {
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);
        given(number.getValue()).willReturn(11D);
        given(number.getStep()).willReturn(1d);

        underTest.validateData(DATA);
    }
}