package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CheckedTableRowDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKED_ROW_ID = UUID.randomUUID();
    private static final UUID UNCHECKED_ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID_1 = UUID.randomUUID();
    private static final UUID COLUMN_ID_2 = UUID.randomUUID();
    private static final String VALUE = "value";

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private ContentDao contentDao;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private TableRowDao tableRowDao;

    @Mock
    private ColumnDataService columnDataService;

    @InjectMocks
    private CheckedTableRowDeletionService underTest;

    @Test
    void deleteCheckedRows() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title("title")
            .build();

        TableColumn column1 = TableColumn.builder()
            .columnId(COLUMN_ID_1)
            .type(ColumnType.TEXT)
            .index(0)
            .build();

        TableColumn column2 = TableColumn.builder()
            .columnId(COLUMN_ID_2)
            .type(ColumnType.EMPTY)
            .index(1)
            .build();

        TableRow checkedRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(CHECKED_ROW_ID)
            .checked(true)
            .columns(List.of(column1, column2))
            .build();

        TableRow uncheckedRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(UNCHECKED_ROW_ID)
            .checked(false)
            .columns(List.of())
            .build();

        Map<UUID, String> contentMap = new HashMap<>();
        contentMap.put(COLUMN_ID_1, VALUE);

        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(1)
            .content(contentMap)
            .modified(false)
            .build();

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> tableData = new QuadWrapper<>(
            listItem,
            List.of(),
            List.of(checkedRow, uncheckedRow),
            List.of(content)
        );

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(tableData);
        given(columnDataServiceProvider.getForType(ColumnType.TEXT)).willReturn(columnDataService);

        underTest.deleteCheckedRows(USER_ID, LIST_ITEM_ID);

        then(columnDataService).should().deleteData(VALUE);
        then(contentDao).should().save(LIST_ITEM_ID, List.of(content));
        then(tableRowDao).should().delete(LIST_ITEM_ID, List.of(checkedRow));
    }
}