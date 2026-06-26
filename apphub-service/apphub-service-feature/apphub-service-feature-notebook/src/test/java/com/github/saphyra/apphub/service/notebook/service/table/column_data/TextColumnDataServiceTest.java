package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class TextColumnDataServiceTest {
    private static final String DATA = "some-text";

    @InjectMocks
    private TextColumnDataService underTest;

    @Test
    void canProcess_text() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isTrue();
    }

    @Test
    void canProcess_checkbox() {
        assertThat(underTest.canProcess(ColumnType.CHECKBOX)).isTrue();
    }

    @Test
    void canProcess_color() {
        assertThat(underTest.canProcess(ColumnType.COLOR)).isTrue();
    }

    @Test
    void canProcess_date() {
        assertThat(underTest.canProcess(ColumnType.DATE)).isTrue();
    }

    @Test
    void canProcess_time() {
        assertThat(underTest.canProcess(ColumnType.TIME)).isTrue();
    }

    @Test
    void canProcess_dateTime() {
        assertThat(underTest.canProcess(ColumnType.DATE_TIME)).isTrue();
    }

    @Test
    void canProcess_month() {
        assertThat(underTest.canProcess(ColumnType.MONTH)).isTrue();
    }

    @Test
    void canProcess_notSupported() {
        assertThat(underTest.canProcess(ColumnType.NUMBER)).isFalse();
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "data", "must not be null");
    }

    @Test
    void validateData_tooLong() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validateData("a".repeat(NotebookConstants.MAX_CONTENT_LENGTH + 1)), "data", "too long");
    }

    @Test
    void validateData() {
        underTest.validateData(DATA);
    }

    @Test
    void serialize() {
        Optional<BiWrapper<String, Optional<UUID>>> result = underTest.serialize(DATA);

        assertThat(result).isPresent();
        assertThat(result.get().getEntity1()).isEqualTo(DATA);
        assertThat(result.get().getEntity2()).isEmpty();
    }

    @Test
    void deserialize() {
        Object result = underTest.deserialize(DATA);

        assertThat(result).isEqualTo(DATA);
    }
}

