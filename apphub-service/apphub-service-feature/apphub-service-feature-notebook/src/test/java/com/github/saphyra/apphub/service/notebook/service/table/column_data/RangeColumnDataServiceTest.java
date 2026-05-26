package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RangeColumnDataServiceTest {
    private static final Object DATA = "data";
    private static final String STRINGIFIED = "stringified";

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RangeColumnDataService underTest;

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.RANGE)).isTrue();
    }

    @Test
    void canProcess_notRange() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isFalse();
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "range", "must not be null");
    }

    @Test
    void validateData_parseError() {
        given(objectMapper.convertValue(DATA, Range.class)).willThrow(new RuntimeException("parse error"));

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range", "failed to parse");
    }

    @Test
    void validateData_nullStep() {
        Range range = Range.builder().step(null).min(1d).max(10d).value(5d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.step", "must not be null");
    }

    @Test
    void validateData_stepTooLow() {
        Range range = Range.builder().step(0d).min(1d).max(10d).value(5d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.step", "too low");
    }

    @Test
    void validateData_nullMin() {
        Range range = Range.builder().step(1d).min(null).max(10d).value(5d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.min", "must not be null");
    }

    @Test
    void validateData_maxTooLow() {
        Range range = Range.builder().step(1d).min(5d).max(4d).value(5d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.max", "too low");
    }

    @Test
    void validateData_valueTooLow() {
        Range range = Range.builder().step(1d).min(1d).max(10d).value(0d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.value", "too low");
    }

    @Test
    void validateData_valueTooHigh() {
        Range range = Range.builder().step(1d).min(1d).max(10d).value(11d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "range.value", "too high");
    }

    @Test
    void validateData() {
        Range range = Range.builder().step(1d).min(1d).max(10d).value(5d).build();
        given(objectMapper.convertValue(DATA, Range.class)).willReturn(range);

        underTest.validateData(DATA);
    }

    @Test
    void serialize() {
        given(objectMapper.writeValueAsString(DATA)).willReturn(STRINGIFIED);

        Optional<BiWrapper<String, Optional<UUID>>> result = underTest.serialize(DATA);

        assertThat(result).isPresent();
        assertThat(result.get().getEntity1()).isEqualTo(STRINGIFIED);
        assertThat(result.get().getEntity2()).isEmpty();
    }

    @Test
    void deserialize() {
        Range range = Range.builder().step(1d).min(1d).max(10d).value(5d).build();
        given(objectMapper.readValue(STRINGIFIED, Range.class)).willReturn(range);

        Object result = underTest.deserialize(STRINGIFIED);

        assertThat(result).isEqualTo(range);
    }
}

