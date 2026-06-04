package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumnFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCloneServiceTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final UUID CLONED_TABLE_HEAD_ID = UUID.randomUUID();
    private static final String HEAD_CONTENT = "head-content";
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID CLONED_COLUMN_ID = UUID.randomUUID();
    private static final int ROW_INDEX = 0;
    private static final int COLUMN_INDEX = 1;
    private static final String COLUMN_CONTENT = "column-content";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final UUID CLONED_FILE_ID = UUID.randomUUID();
    private static final String CLONED_FILE_ID_STRING = CLONED_FILE_ID.toString();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private TableHeadFactory tableHeadFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private TableRowFactory tableRowFactory;

    @Mock
    private TableColumnFactory tableColumnFactory;

    @Mock
    private StorageProxy storageProxy;

    @Mock
    private ContentDao contentDao;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableRowDao tableRowDao;

    @InjectMocks
    private TableCloneService underTest;

    @Mock
    private ListItem toClone;

    @Mock
    private ListItem clonedListItem;

    @Mock
    private TableHead originalTableHead;

    @Mock
    private TableHead clonedTableHead;

    @Mock
    private TableRow originalRow;

    @Mock
    private TableRow clonedRow;

    @Mock
    private TableColumn originalColumn;

    @Mock
    private TableColumn clonedColumn;

    @Mock
    private Content originalContent;

    @Mock
    private Content headContent;

    @Mock
    private Content columnContent;

    @Test
    void cloneTable_withTableHeads() {
        given(toClone.getUserId()).willReturn(USER_ID);
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(Map.of(TABLE_HEAD_ID, HEAD_CONTENT));

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table =
            QuadWrapper.<ListItem, List<TableHead>, List<TableRow>, List<Content>>builder()
                .entity2(List.of(originalTableHead))
                .entity3(List.of())
                .entity4(List.of(originalContent))
                .build();

        given(commonListItemDao.findTableValidated(USER_ID, ORIGINAL_LIST_ITEM_ID)).willReturn(table);
        given(originalTableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);
        given(tableHeadFactory.clone(originalTableHead)).willReturn(clonedTableHead);
        given(clonedTableHead.getTableHeadId()).willReturn(CLONED_TABLE_HEAD_ID);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_TABLE_HEAD_ID, HEAD_CONTENT)).willReturn(headContent);

        underTest.cloneTable(PARENT, toClone);

        then(listItemDao).should().save(clonedListItem);
        then(tableHeadDao).should().save(CLONED_LIST_ITEM_ID, List.of(clonedTableHead));
        then(contentDao).should().save(CLONED_LIST_ITEM_ID, List.of(headContent));
    }

    @Test
    void cloneTable_withTextColumn() {
        given(toClone.getUserId()).willReturn(USER_ID);
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(Map.of(COLUMN_ID, COLUMN_CONTENT));

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table =
            QuadWrapper.<ListItem, List<TableHead>, List<TableRow>, List<Content>>builder()
                .entity2(List.of())
                .entity3(List.of(originalRow))
                .entity4(List.of(originalContent))
                .build();

        given(commonListItemDao.findTableValidated(USER_ID, ORIGINAL_LIST_ITEM_ID)).willReturn(table);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(originalRow.getChecked()).willReturn(null);
        given(originalRow.getColumns()).willReturn(List.of(originalColumn));
        given(originalColumn.getIndex()).willReturn(COLUMN_INDEX);
        given(originalColumn.getType()).willReturn(ColumnType.TEXT);
        given(originalColumn.getColumnId()).willReturn(COLUMN_ID);
        given(tableColumnFactory.create(COLUMN_INDEX, ColumnType.TEXT)).willReturn(clonedColumn);
        given(clonedColumn.getType()).willReturn(ColumnType.TEXT);
        given(clonedColumn.getColumnId()).willReturn(CLONED_COLUMN_ID);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_COLUMN_ID, COLUMN_CONTENT)).willReturn(columnContent);
        given(tableRowFactory.create(CLONED_LIST_ITEM_ID, ROW_INDEX, null, List.of(clonedColumn))).willReturn(clonedRow);

        underTest.cloneTable(PARENT, toClone);

        then(listItemDao).should().save(clonedListItem);
        then(tableHeadDao).should().save(CLONED_LIST_ITEM_ID, List.of());
        then(tableRowDao).should().save(List.of(clonedRow));
        then(contentDao).should().save(CLONED_LIST_ITEM_ID, List.of(columnContent));
    }

    @Test
    void cloneTable_withEmptyColumn() {
        given(toClone.getUserId()).willReturn(USER_ID);
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(Map.of());

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table =
            QuadWrapper.<ListItem, List<TableHead>, List<TableRow>, List<Content>>builder()
                .entity2(List.of())
                .entity3(List.of(originalRow))
                .entity4(List.of(originalContent))
                .build();

        given(commonListItemDao.findTableValidated(USER_ID, ORIGINAL_LIST_ITEM_ID)).willReturn(table);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(originalRow.getChecked()).willReturn(null);
        given(originalRow.getColumns()).willReturn(List.of(originalColumn));
        given(originalColumn.getIndex()).willReturn(COLUMN_INDEX);
        given(originalColumn.getType()).willReturn(ColumnType.EMPTY);
        given(tableColumnFactory.create(COLUMN_INDEX, ColumnType.EMPTY)).willReturn(clonedColumn);
        given(clonedColumn.getType()).willReturn(ColumnType.EMPTY);
        given(tableRowFactory.create(CLONED_LIST_ITEM_ID, ROW_INDEX, null, List.of(clonedColumn))).willReturn(clonedRow);

        underTest.cloneTable(PARENT, toClone);

        then(listItemDao).should().save(clonedListItem);
        then(tableRowDao).should().save(List.of(clonedRow));
        then(contentDao).should().save(CLONED_LIST_ITEM_ID, List.of());
    }

    @Test
    void cloneTable_withFileColumn() {
        given(toClone.getUserId()).willReturn(USER_ID);
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(Map.of(COLUMN_ID, COLUMN_CONTENT));

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table =
            QuadWrapper.<ListItem, List<TableHead>, List<TableRow>, List<Content>>builder()
                .entity2(List.of())
                .entity3(List.of(originalRow))
                .entity4(List.of(originalContent))
                .build();

        given(commonListItemDao.findTableValidated(USER_ID, ORIGINAL_LIST_ITEM_ID)).willReturn(table);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(originalRow.getChecked()).willReturn(null);
        given(originalRow.getColumns()).willReturn(List.of(originalColumn));
        given(originalColumn.getIndex()).willReturn(COLUMN_INDEX);
        given(originalColumn.getType()).willReturn(ColumnType.FILE);
        given(originalColumn.getColumnId()).willReturn(COLUMN_ID);
        given(tableColumnFactory.create(COLUMN_INDEX, ColumnType.FILE)).willReturn(clonedColumn);
        given(clonedColumn.getType()).willReturn(ColumnType.FILE);
        given(clonedColumn.getColumnId()).willReturn(CLONED_COLUMN_ID);
        given(uuidConverter.convertEntity(COLUMN_CONTENT)).willReturn(STORED_FILE_ID);
        given(storageProxy.cloneFile(STORED_FILE_ID)).willReturn(CLONED_FILE_ID);
        given(uuidConverter.convertDomain(CLONED_FILE_ID)).willReturn(CLONED_FILE_ID_STRING);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_COLUMN_ID, CLONED_FILE_ID_STRING)).willReturn(columnContent);
        given(tableRowFactory.create(CLONED_LIST_ITEM_ID, ROW_INDEX, null, List.of(clonedColumn))).willReturn(clonedRow);

        underTest.cloneTable(PARENT, toClone);

        then(listItemDao).should().save(clonedListItem);
        then(tableRowDao).should().save(List.of(clonedRow));
        then(contentDao).should().save(CLONED_LIST_ITEM_ID, List.of(columnContent));
    }

    @Test
    void cloneTable_withImageColumn() {
        given(toClone.getUserId()).willReturn(USER_ID);
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(Map.of(COLUMN_ID, COLUMN_CONTENT));

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table =
            QuadWrapper.<ListItem, List<TableHead>, List<TableRow>, List<Content>>builder()
                .entity2(List.of())
                .entity3(List.of(originalRow))
                .entity4(List.of(originalContent))
                .build();

        given(commonListItemDao.findTableValidated(USER_ID, ORIGINAL_LIST_ITEM_ID)).willReturn(table);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(originalRow.getChecked()).willReturn(null);
        given(originalRow.getColumns()).willReturn(List.of(originalColumn));
        given(originalColumn.getIndex()).willReturn(COLUMN_INDEX);
        given(originalColumn.getType()).willReturn(ColumnType.IMAGE);
        given(originalColumn.getColumnId()).willReturn(COLUMN_ID);
        given(tableColumnFactory.create(COLUMN_INDEX, ColumnType.IMAGE)).willReturn(clonedColumn);
        given(clonedColumn.getType()).willReturn(ColumnType.IMAGE);
        given(clonedColumn.getColumnId()).willReturn(CLONED_COLUMN_ID);
        given(uuidConverter.convertEntity(COLUMN_CONTENT)).willReturn(STORED_FILE_ID);
        given(storageProxy.cloneFile(STORED_FILE_ID)).willReturn(CLONED_FILE_ID);
        given(uuidConverter.convertDomain(CLONED_FILE_ID)).willReturn(CLONED_FILE_ID_STRING);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_COLUMN_ID, CLONED_FILE_ID_STRING)).willReturn(columnContent);
        given(tableRowFactory.create(CLONED_LIST_ITEM_ID, ROW_INDEX, null, List.of(clonedColumn))).willReturn(clonedRow);

        underTest.cloneTable(PARENT, toClone);

        then(listItemDao).should().save(clonedListItem);
        then(tableRowDao).should().save(List.of(clonedRow));
        then(contentDao).should().save(CLONED_LIST_ITEM_ID, List.of(columnContent));
    }

    @Test
    void cloneTable_withCheckedRow() {
        given(toClone.getUserId()).willReturn(USER_ID);
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(Map.of());

        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table =
            QuadWrapper.<ListItem, List<TableHead>, List<TableRow>, List<Content>>builder()
                .entity2(List.of())
                .entity3(List.of(originalRow))
                .entity4(List.of(originalContent))
                .build();

        given(commonListItemDao.findTableValidated(USER_ID, ORIGINAL_LIST_ITEM_ID)).willReturn(table);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(originalRow.getChecked()).willReturn(true);
        given(originalRow.getColumns()).willReturn(List.of());
        given(tableRowFactory.create(CLONED_LIST_ITEM_ID, ROW_INDEX, true, List.of())).willReturn(clonedRow);

        underTest.cloneTable(PARENT, toClone);

        then(listItemDao).should().save(clonedListItem);
        then(tableRowDao).should().save(List.of(clonedRow));
        then(contentDao).should().save(CLONED_LIST_ITEM_ID, List.of());
    }
}

