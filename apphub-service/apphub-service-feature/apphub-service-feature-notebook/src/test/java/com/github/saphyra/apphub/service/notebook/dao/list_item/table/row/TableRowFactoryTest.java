package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableRowFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_ROW_ID = UUID.randomUUID();
    private static final int INDEX = 1;
    private static final Boolean CHECKED = true;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private TableRowFactory underTest;

    @Mock
    private TableColumn column;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(TABLE_ROW_ID);

        TableRow result = underTest.create(LIST_ITEM_ID, INDEX, CHECKED, List.of(column));

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTableRowId()).isEqualTo(TABLE_ROW_ID);
        assertThat(result.getIndex()).isEqualTo(INDEX);
        assertThat(result.getChecked()).isEqualTo(CHECKED);
        assertThat(result.getColumns()).containsExactly(column);
    }
}

