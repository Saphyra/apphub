package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableColumnFactoryTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final int INDEX = 2;
    private static final ColumnType TYPE = ColumnType.TEXT;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private TableColumnFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(COLUMN_ID);

        TableColumn result = underTest.create(INDEX, TYPE);

        assertThat(result.getColumnId()).isEqualTo(COLUMN_ID);
        assertThat(result.getIndex()).isEqualTo(INDEX);
        assertThat(result.getType()).isEqualTo(TYPE);
    }
}

