package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableRowEditerTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Integer INDEX = 24;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private CheckedItemFactory checkedItemFactory;

    @Mock
    private EditTableColumnService editTableColumnService;

    @InjectMocks
    private EditTableRowEditer underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableRowModel rowModel;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private Dimension row;

    @Mock
    private TableColumnModel columnModel;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void existingRow_table() {
        given(rowModel.getItemType()).willReturn(ItemType.EXISTING);
        given(rowModel.getRowId()).willReturn(ROW_ID);
        given(rowModel.getRowIndex()).willReturn(INDEX);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(listItem.getType()).willReturn(ListItemType.TABLE);
        given(dimensionDao.findByIdValidated(ROW_ID)).willReturn(row);
        given(editTableColumnService.editTableColumns(listItem, ROW_ID, List.of(columnModel))).willReturn(List.of(fileUploadResponse));
        given(row.getDimensionId()).willReturn(ROW_ID);

        assertThat(underTest.updateRows(listItem, List.of(rowModel))).containsExactly(fileUploadResponse);

        then(row).should().setIndex(INDEX);
        then(dimensionDao).should().save(row);
        then(checkedItemDao).shouldHaveNoInteractions();
    }

    @Test
    void newRow_table() {
        given(rowModel.getItemType()).willReturn(ItemType.NEW);
        given(rowModel.getRowIndex()).willReturn(INDEX);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(listItem.getType()).willReturn(ListItemType.TABLE);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(editTableColumnService.editTableColumns(listItem, ROW_ID, List.of(columnModel))).willReturn(List.of(fileUploadResponse));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, INDEX)).willReturn(row);

        assertThat(underTest.updateRows(listItem, List.of(rowModel))).containsExactly(fileUploadResponse);

        then(dimensionDao).should().save(row);
        then(checkedItemDao).shouldHaveNoInteractions();
    }

    @Test
    void existingRow_checklistTable() {
        given(rowModel.getItemType()).willReturn(ItemType.EXISTING);
        given(rowModel.getRowId()).willReturn(ROW_ID);
        given(rowModel.getRowIndex()).willReturn(INDEX);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(rowModel.getChecked()).willReturn(true);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(dimensionDao.findByIdValidated(ROW_ID)).willReturn(row);
        given(editTableColumnService.editTableColumns(listItem, ROW_ID, List.of(columnModel))).willReturn(List.of(fileUploadResponse));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(checkedItemDao.findByIdValidated(ROW_ID)).willReturn(checkedItem);

        assertThat(underTest.updateRows(listItem, List.of(rowModel))).containsExactly(fileUploadResponse);

        then(row).should().setIndex(INDEX);
        then(dimensionDao).should().save(row);
        then(checkedItem).should().setChecked(true);
        then(checkedItemDao).should().save(checkedItem);
    }

    @Test
    void newRow_checklistTable() {
        given(rowModel.getItemType()).willReturn(ItemType.NEW);
        given(rowModel.getRowIndex()).willReturn(INDEX);
        given(rowModel.getColumns()).willReturn(List.of(columnModel));
        given(rowModel.getChecked()).willReturn(true);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(editTableColumnService.editTableColumns(listItem, ROW_ID, List.of(columnModel))).willReturn(List.of(fileUploadResponse));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, INDEX)).willReturn(row);
        given(checkedItemFactory.create(USER_ID, ROW_ID, true)).willReturn(checkedItem);

        assertThat(underTest.updateRows(listItem, List.of(rowModel))).containsExactly(fileUploadResponse);

        then(dimensionDao).should().save(row);
        then(checkedItemDao).should().save(checkedItem);
    }
}