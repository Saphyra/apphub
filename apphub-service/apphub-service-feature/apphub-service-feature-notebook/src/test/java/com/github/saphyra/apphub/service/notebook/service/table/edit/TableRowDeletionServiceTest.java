package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableRowDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final String COLUMN_DATA = "column-data";

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private TableRowDao tableRowDao;

    @Mock
    private ColumnDataService columnDataService;

    @InjectMocks
    private TableRowDeletionService underTest;

    @Test
    void processTableRowDeletion() {
        TableColumn tableColumn = TableColumn.builder()
            .columnId(COLUMN_ID)
            .index(1)
            .type(ColumnType.FILE)
            .build();
        TableRow tableRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .index(0)
            .columns(List.of(tableColumn))
            .build();
        List<TableRow> tableRows = new ArrayList<>(List.of(tableRow));
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(COLUMN_ID, COLUMN_DATA);

        given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);

        underTest.processTableRowDeletion(LIST_ITEM_ID, List.of(), tableRows, List.of(content));

        assertThat(tableRows).isEmpty();
        assertThat(content.contains(COLUMN_ID)).isFalse();
        then(columnDataService).should().deleteData(COLUMN_DATA);
        then(tableRowDao).should().delete(LIST_ITEM_ID, ROW_ID);
    }
}

