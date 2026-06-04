package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
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
class TableColumnDeletionServiceTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final String COLUMN_DATA = "column-data";

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private ColumnDataService columnDataService;

    @InjectMocks
    private TableColumnDeletionService underTest;

    @Test
    void processTableColumnDeletion_deleted() {
        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .index(2)
            .type(ColumnType.FILE)
            .build();
        List<TableColumn> columns = new ArrayList<>(List.of(column));
        Content content = Content.builder()
            .listItemId(UUID.randomUUID())
            .build()
            .add(COLUMN_ID, COLUMN_DATA);
        List<Content> contents = List.of(content);

        given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);

        boolean result = underTest.processTableColumnDeletion(List.of(), columns, contents);

        assertThat(result).isTrue();
        assertThat(columns).isEmpty();
        assertThat(content.contains(COLUMN_ID)).isFalse();
        then(columnDataService).should().deleteData(COLUMN_DATA);
    }
}

