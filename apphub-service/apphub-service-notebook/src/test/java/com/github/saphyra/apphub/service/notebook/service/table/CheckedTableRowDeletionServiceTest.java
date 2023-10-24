package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableRowDeletionService;
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
class CheckedTableRowDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private TableRowDeletionService tableRowDeletionService;

    @InjectMocks
    private CheckedTableRowDeletionService underTest;

    @Mock
    private Dimension row;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void deleteCheckedRows_checked() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(row));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(checkedItemDao.findByIdValidated(ROW_ID)).willReturn(checkedItem);
        given(checkedItem.getChecked()).willReturn(true);

        underTest.deleteCheckedRows(LIST_ITEM_ID);

        then(tableRowDeletionService).should().deleteRow(row);
    }

    @Test
    void deleteCheckedRows_notChecked() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(row));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(checkedItemDao.findByIdValidated(ROW_ID)).willReturn(checkedItem);
        given(checkedItem.getChecked()).willReturn(false);

        underTest.deleteCheckedRows(LIST_ITEM_ID);

        then(tableRowDeletionService).shouldHaveNoInteractions();
    }
}