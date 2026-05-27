package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final Integer TABLE_HEAD_INDEX = 2;
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final UUID TABLE_ROW_ID = UUID.randomUUID();
    private static final Integer TABLE_ROW_INDEX = 3;
    private static final UUID COLUMN_WITH_CONTENT_ID = UUID.randomUUID();
    private static final Integer COLUMN_WITH_CONTENT_INDEX = 4;
    private static final UUID COLUMN_WITHOUT_CONTENT_ID = UUID.randomUUID();
    private static final Integer COLUMN_WITHOUT_CONTENT_INDEX = 5;
    private static final String SERIALIZED_COLUMN_DATA = "serialized-column-data";
    private static final Object DESERIALIZED_COLUMN_DATA = "deserialized-column-data";

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private CommonListItemDao commonListItemDao;

    @InjectMocks
    private TableQueryService underTest;

    @Mock
    private ColumnDataService columnDataService;

    @Test
    void getTable() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.TABLE)
            .title(TITLE)
            .build();

        TableHead tableHead = TableHead.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .index(TABLE_HEAD_INDEX)
            .build();

        TableColumn columnWithContent = TableColumn.builder()
            .columnId(COLUMN_WITH_CONTENT_ID)
            .index(COLUMN_WITH_CONTENT_INDEX)
            .type(ColumnType.TEXT)
            .build();

        TableColumn columnWithoutContent = TableColumn.builder()
            .columnId(COLUMN_WITHOUT_CONTENT_ID)
            .index(COLUMN_WITHOUT_CONTENT_INDEX)
            .type(ColumnType.NUMBER)
            .build();

        TableRow tableRow = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(TABLE_ROW_ID)
            .index(TABLE_ROW_INDEX)
            .checked(true)
            .columns(List.of(columnWithContent, columnWithoutContent))
            .build();

        Content tableHeadContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(Map.of(TABLE_HEAD_ID, TABLE_HEAD_CONTENT))
            .build();

        Content columnData = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(Map.of(COLUMN_WITH_CONTENT_ID, SERIALIZED_COLUMN_DATA))
            .build();

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(new QuadWrapper<>(listItem, List.of(tableHead), List.of(tableRow), List.of(tableHeadContent, columnData)));
        given(columnDataServiceProvider.getForType(ColumnType.TEXT)).willReturn(columnDataService);
        given(columnDataService.deserialize(SERIALIZED_COLUMN_DATA)).willReturn(DESERIALIZED_COLUMN_DATA);

        TableResponse result = underTest.getTable(USER_ID, LIST_ITEM_ID);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getParent()).isEqualTo(PARENT);

        TableHeadModel tableHeadModel = result.getTableHeads().getFirst();
        assertThat(tableHeadModel.getTableHeadId()).isEqualTo(TABLE_HEAD_ID);
        assertThat(tableHeadModel.getColumnIndex()).isEqualTo(TABLE_HEAD_INDEX);
        assertThat(tableHeadModel.getContent()).isEqualTo(TABLE_HEAD_CONTENT);
        assertThat(tableHeadModel.getType()).isEqualTo(ItemType.EXISTING);

        TableRowModel tableRowModel = result.getRows().getFirst();
        assertThat(tableRowModel.getRowId()).isEqualTo(TABLE_ROW_ID);
        assertThat(tableRowModel.getRowIndex()).isEqualTo(TABLE_ROW_INDEX);
        assertThat(tableRowModel.getChecked()).isTrue();
        assertThat(tableRowModel.getItemType()).isEqualTo(ItemType.EXISTING);

        TableColumnModel firstColumn = tableRowModel.getColumns().getFirst();
        assertThat(firstColumn.getColumnId()).isEqualTo(COLUMN_WITH_CONTENT_ID);
        assertThat(firstColumn.getColumnIndex()).isEqualTo(COLUMN_WITH_CONTENT_INDEX);
        assertThat(firstColumn.getColumnType()).isEqualTo(ColumnType.TEXT);
        assertThat(firstColumn.getItemType()).isEqualTo(ItemType.EXISTING);
        assertThat(firstColumn.getData()).isEqualTo(DESERIALIZED_COLUMN_DATA);

        TableColumnModel secondColumn = tableRowModel.getColumns().get(1);
        assertThat(secondColumn.getColumnId()).isEqualTo(COLUMN_WITHOUT_CONTENT_ID);
        assertThat(secondColumn.getColumnIndex()).isEqualTo(COLUMN_WITHOUT_CONTENT_INDEX);
        assertThat(secondColumn.getColumnType()).isEqualTo(ColumnType.NUMBER);
        assertThat(secondColumn.getItemType()).isEqualTo(ItemType.EXISTING);
        assertThat(secondColumn.getData()).isNull();

        then(commonListItemDao).should().findTableValidated(USER_ID, LIST_ITEM_ID);
        then(columnDataServiceProvider).should().getForType(ColumnType.TEXT);
        then(columnDataService).should().deserialize(SERIALIZED_COLUMN_DATA);
    }

    @Test
    void getTable_tableHeadContentNotFound() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .type(ListItemType.TABLE)
            .title(TITLE)
            .build();

        TableHead tableHead = TableHead.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .index(TABLE_HEAD_INDEX)
            .build();

        given(commonListItemDao.findTableValidated(USER_ID, LIST_ITEM_ID)).willReturn(new QuadWrapper<>(listItem, List.of(tableHead), List.of(), List.of()));

        Throwable ex = catchThrowable(() -> underTest.getTable(USER_ID, LIST_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}