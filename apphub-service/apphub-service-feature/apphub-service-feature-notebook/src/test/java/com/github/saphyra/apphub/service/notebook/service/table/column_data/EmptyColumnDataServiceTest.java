package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmptyColumnDataServiceTest {
    @InjectMocks
    private EmptyColumnDataService underTest;

    @Test
    void canProcess_empty() {
        assertThat(underTest.canProcess(ColumnType.EMPTY)).isTrue();
    }

    @Test
    void canProcess_notEmpty() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isFalse();
    }

    @Test
    void validateData_doesNothing() {
        underTest.validateData("anything");
    }

    @Test
    void serialize_returnsEmpty() {
        Optional<BiWrapper<String, Optional<UUID>>> result = underTest.serialize("data");

        assertThat(result).isEmpty();
    }

    @Test
    void deserialize_returnsNull() {
        Object result = underTest.deserialize("data");

        assertThat(result).isNull();
    }
}

