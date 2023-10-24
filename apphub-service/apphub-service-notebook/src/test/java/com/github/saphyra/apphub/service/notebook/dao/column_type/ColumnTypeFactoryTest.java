package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ColumnTypeFactoryTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private ColumnTypeFactory underTest;

    @Test
    void create() {
        ColumnTypeDto result = underTest.create(COLUMN_ID, USER_ID, ColumnType.TEXT);

        assertThat(result.getColumnId()).isEqualTo(COLUMN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getType()).isEqualTo(ColumnType.TEXT);
    }
}