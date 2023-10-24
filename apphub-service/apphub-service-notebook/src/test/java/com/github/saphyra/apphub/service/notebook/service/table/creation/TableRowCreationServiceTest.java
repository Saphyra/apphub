package com.github.saphyra.apphub.service.notebook.service.table.creation;

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
class TableRowCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 542;
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemFactory checkedItemFactory;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private TableColumnCreationService tableColumnCreationService;

    @InjectMocks
    private TableRowCreationService underTest;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private TableRowModel model;

    @Mock
    private Dimension row;

    @Mock
    private TableColumnModel column;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void saveRows_table() {
        given(model.getRowIndex()).willReturn(ROW_INDEX);
        given(model.getColumns()).willReturn(List.of(column));
        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, ROW_INDEX)).willReturn(row);
        given(tableColumnCreationService.saveColumns(USER_ID, LIST_ITEM_ID, row, List.of(column))).willReturn(List.of(fileUploadResponse));

        assertThat(underTest.saveRows(USER_ID, LIST_ITEM_ID, List.of(model), ListItemType.TABLE)).containsExactly(fileUploadResponse);

        then(dimensionDao).should().save(row);
        then(checkedItemDao).shouldHaveNoInteractions();
    }

    @Test
    void saveRows_checklistTable() {
        given(model.getRowIndex()).willReturn(ROW_INDEX);
        given(model.getColumns()).willReturn(List.of(column));
        given(dimensionFactory.create(USER_ID, LIST_ITEM_ID, ROW_INDEX)).willReturn(row);
        given(tableColumnCreationService.saveColumns(USER_ID, LIST_ITEM_ID, row, List.of(column))).willReturn(List.of(fileUploadResponse));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(model.getChecked()).willReturn(true);
        given(checkedItemFactory.create(USER_ID, ROW_ID, true)).willReturn(checkedItem);

        assertThat(underTest.saveRows(USER_ID, LIST_ITEM_ID, List.of(model), ListItemType.CHECKLIST_TABLE)).containsExactly(fileUploadResponse);

        then(dimensionDao).should().save(row);
        then(checkedItemDao).should().save(checkedItem);
    }
}