package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableCreationRequestValidator;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String TABLE_HEAD_CONTENT = "table-head-content";
    private static final Object FILE_COLUMN_DATA = "file-column-data";
    private static final Integer TABLE_HEAD_COLUMN_INDEX = 0;
    private static final @NonNull UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final @NonNull UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 1;
    private static final Integer FILE_COLUMN_INDEX = 2;
    private static final Integer EMPTY_COLUMN_INDEX = 3;
    private static final String SERIALIZED_FILE_COLUMN_DATA = "serialized-file-column-data";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final @NonNull UUID FILE_COLUMN_ID = UUID.randomUUID();

    @Mock
    private TableCreationRequestValidator tableCreationRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private TableHeadFactory tableHeadFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private TableRowFactory tableRowFactory;

    @Mock
    private TableColumnFactory tableColumnFactory;

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private ColumnDataService columnDataService;

    @InjectMocks
    private TableCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead tableHead;

    @Mock
    private Content tableHeadContent;

    @Mock
    private TableRow tableRow;

    @Mock
    private TableColumn fileColumn;

    @Mock
    private TableColumn emptyColumn;

    @Mock
    private Content fileColumnContent;

    @Test
    void create() {
        CreateTableRequest request = CreateTableRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(TABLE_HEAD_COLUMN_INDEX)
                .content(TABLE_HEAD_CONTENT)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(ROW_INDEX)
                .checked(true)
                .columns(List.of(
                    TableColumnModel.builder()
                        .columnIndex(FILE_COLUMN_INDEX)
                        .columnType(ColumnType.FILE)
                        .data(FILE_COLUMN_DATA)
                        .build(),
                    TableColumnModel.builder()
                        .columnIndex(EMPTY_COLUMN_INDEX)
                        .columnType(ColumnType.EMPTY)
                        .build()
                ))
                .build()))
            .build();

        given(listItemFactory.create(USER_ID, PARENT, TITLE, ListItemType.TABLE)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(tableHeadFactory.create(TABLE_HEAD_COLUMN_INDEX)).willReturn(tableHead);
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(contentFactory.create(LIST_ITEM_ID, TABLE_HEAD_ID, TABLE_HEAD_CONTENT)).willReturn(tableHeadContent);
        given(tableColumnFactory.create(FILE_COLUMN_INDEX, ColumnType.FILE)).willReturn(fileColumn);
        given(tableColumnFactory.create(EMPTY_COLUMN_INDEX, ColumnType.EMPTY)).willReturn(emptyColumn);
        given(columnDataServiceProvider.getForType(ColumnType.FILE)).willReturn(columnDataService);
        given(columnDataServiceProvider.getForType(ColumnType.EMPTY)).willReturn(columnDataService);
        given(columnDataService.serialize(FILE_COLUMN_DATA)).willReturn(Optional.of(new BiWrapper<>(SERIALIZED_FILE_COLUMN_DATA, Optional.of(STORED_FILE_ID))));
        given(columnDataService.serialize(null)).willReturn(Optional.empty());
        given(fileColumn.getColumnId()).willReturn(FILE_COLUMN_ID);
        given(contentFactory.create(LIST_ITEM_ID, FILE_COLUMN_ID, SERIALIZED_FILE_COLUMN_DATA)).willReturn(fileColumnContent);
        given(tableRowFactory.create(LIST_ITEM_ID, ROW_INDEX, true, List.of(fileColumn, emptyColumn))).willReturn(tableRow);

        CustomAssertions.singleListAssertThat(underTest.create(USER_ID, request))
            .returns(ROW_INDEX, TableFileUploadResponse::getRowIndex)
            .returns(FILE_COLUMN_INDEX, TableFileUploadResponse::getColumnIndex)
            .returns(STORED_FILE_ID, TableFileUploadResponse::getStoredFileId);

        then(tableCreationRequestValidator).should().validate(USER_ID, request);
        then(commonListItemDao).should().saveTable(listItem, List.of(tableHead), List.of(tableRow), List.of(tableHeadContent, fileColumnContent));
    }
}