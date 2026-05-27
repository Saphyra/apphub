package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
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
class NumberColumnDataServiceTest {
    private static final Object DATA = "data";
    private static final String STRINGIFIED = "stringified";

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NumberColumnDataService underTest;

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.NUMBER)).isTrue();
    }

    @Test
    void canProcess_notNumber() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isFalse();
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "number", "must not be null");
    }

    @Test
    void validateData_parseError() {
        given(objectMapper.convertValue(DATA, Number.class)).willThrow(new RuntimeException("parse error"));

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number", "failed to parse");
    }

    @Test
    void validateData_nullValue() {
        Number number = Number.builder().value(null).step(1d).build();
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number.value", "must not be null");
    }

    @Test
    void validateData_nullStep() {
        Number number = Number.builder().value(5d).step(null).build();
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number.step", "must not be null");
    }

    @Test
    void validateData_stepTooLow() {
        Number number = Number.builder().value(5d).step(0d).build();
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "number.step", "too low");
    }

    @Test
    void validateData() {
        Number number = Number.builder().value(5d).step(1d).build();
        given(objectMapper.convertValue(DATA, Number.class)).willReturn(number);

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
        Number number = Number.builder().value(5d).step(1d).build();
        given(objectMapper.readValue(STRINGIFIED, Number.class)).willReturn(number);

        Object result = underTest.deserialize(STRINGIFIED);

        assertThat(result).isEqualTo(number);
    }
}

