package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TableDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final String VALUE = "value";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableRowDao tableRowDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private ColumnDataService columnDataService;

    @InjectMocks
    private TableDeletionService underTest;

    @Test
    void delete_columnDataDeleted() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title("title")
            .build();

        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .type(ColumnType.TEXT)
            .index(0)
            .build();

        TableRow row = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(UUID.randomUUID())
            .columns(List.of(column))
            .build();

        Map<UUID, String> contentMap = new HashMap<>();
        contentMap.put(COLUMN_ID, VALUE);

        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .content(contentMap)
            .modified(false)
            .build();

        List<TableHead> tableHeads = List.of();
        List<TableRow> rows = List.of(row);
        List<Content> contents = List.of(content);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = new QuadWrapper<>(listItem, tableHeads, rows, contents);

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(table);
        given(columnDataServiceProvider.getForType(ColumnType.TEXT)).willReturn(columnDataService);

        underTest.delete(listItem);

        then(columnDataService).should().deleteData(VALUE);
        then(listItemDao).should().delete(listItem);
        then(tableHeadDao).should().delete(LIST_ITEM_ID);
        then(tableRowDao).should().delete(LIST_ITEM_ID, rows);
        then(contentDao).should().delete(LIST_ITEM_ID, contents);
    }

    @Test
    void delete_noContentForColumn() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.TABLE)
            .title("title")
            .build();

        TableColumn column = TableColumn.builder()
            .columnId(COLUMN_ID)
            .type(ColumnType.TEXT)
            .index(0)
            .build();

        TableRow row = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(UUID.randomUUID())
            .columns(List.of(column))
            .build();

        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .modified(false)
            .build();

        List<TableHead> tableHeads = List.of();
        List<TableRow> rows = List.of(row);
        List<Content> contents = List.of(content);

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = new QuadWrapper<>(listItem, tableHeads, rows, contents);

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(table);

        underTest.delete(listItem);

        then(columnDataServiceProvider).should(never()).getForType(ColumnType.TEXT);
        then(columnDataService).should(never()).deleteData(VALUE);
        then(listItemDao).should().delete(listItem);
        then(tableHeadDao).should().delete(LIST_ITEM_ID);
        then(tableRowDao).should().delete(LIST_ITEM_ID, rows);
        then(contentDao).should().delete(LIST_ITEM_ID, contents);
    }
}