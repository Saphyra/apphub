package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableRowCloneServiceTest {
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 24;
    private static final UUID ORIGINAL_ROW_ID = UUID.randomUUID();
    private static final UUID CLONED_ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private CheckedItemFactory checkedItemFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private TableColumnCloneService tableColumnCloneService;

    @InjectMocks
    private TableRowCloneService underTest;

    @Mock
    private ListItem originalListItem;

    @Mock
    private ListItem listItemClone;

    @Mock
    private Dimension originalRow;

    @Mock
    private Dimension rowClone;

    @Mock
    private CheckedItem originalCheckedItem;

    @Mock
    private CheckedItem checkedItemClone;

    @Test
    void cloneRowsOfTable() {
        given(originalListItem.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(dimensionDao.getByExternalReference(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalRow));
        given(listItemClone.getUserId()).willReturn(USER_ID);
        given(listItemClone.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(dimensionFactory.create(USER_ID, CLONED_LIST_ITEM_ID, ROW_INDEX)).willReturn(rowClone);
        given(listItemClone.getType()).willReturn(ListItemType.TABLE);

        underTest.cloneRows(originalListItem, listItemClone);

        then(dimensionDao).should().save(rowClone);
        then(tableColumnCloneService).should().cloneColumns(listItemClone, originalRow, rowClone);
        then(checkedItemDao).shouldHaveNoInteractions();
    }

    @Test
    void cloneRowsOfChecklistTable() {
        given(originalListItem.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(dimensionDao.getByExternalReference(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalRow));
        given(listItemClone.getUserId()).willReturn(USER_ID);
        given(listItemClone.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalRow.getIndex()).willReturn(ROW_INDEX);
        given(dimensionFactory.create(USER_ID, CLONED_LIST_ITEM_ID, ROW_INDEX)).willReturn(rowClone);
        given(listItemClone.getType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(originalRow.getDimensionId()).willReturn(ORIGINAL_ROW_ID);
        given(checkedItemDao.findByIdValidated(ORIGINAL_ROW_ID)).willReturn(originalCheckedItem);
        given(rowClone.getDimensionId()).willReturn(CLONED_ROW_ID);
        given(originalCheckedItem.getChecked()).willReturn(true);
        given(checkedItemFactory.create(USER_ID, CLONED_ROW_ID, true)).willReturn(checkedItemClone);

        underTest.cloneRows(originalListItem, listItemClone);

        then(dimensionDao).should().save(rowClone);
        then(tableColumnCloneService).should().cloneColumns(listItemClone, originalRow, rowClone);
        then(checkedItemDao).should().save(checkedItemClone);
    }
}