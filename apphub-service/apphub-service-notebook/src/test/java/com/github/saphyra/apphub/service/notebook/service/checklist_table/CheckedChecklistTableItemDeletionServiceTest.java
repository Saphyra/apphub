package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CheckedChecklistTableItemDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 23;
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private CheckedChecklistTableItemDeletionService underTest;

    @Mock
    private ChecklistTableRow checkedRow;

    @Mock
    private ChecklistTableRow uncheckedRow;

    @Mock
    private TableJoin rowJoin;

    @Mock
    private TableJoin otherRowJoin;

    @Test
    public void deleteCheckedItems() {
        given(checklistTableRowDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checkedRow, uncheckedRow));
        given(uncheckedRow.isChecked()).willReturn(false);
        given(checkedRow.isChecked()).willReturn(true);
        given(checkedRow.getParent()).willReturn(LIST_ITEM_ID);
        given(checkedRow.getRowIndex()).willReturn(ROW_INDEX);

        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(rowJoin, otherRowJoin));
        given(otherRowJoin.getRowIndex()).willReturn(32);
        given(rowJoin.getRowIndex()).willReturn(ROW_INDEX);
        given(rowJoin.getTableJoinId()).willReturn(TABLE_JOIN_ID);

        underTest.deleteCheckedItems(LIST_ITEM_ID);

        verify(checklistTableRowDao).delete(checkedRow);
        verify(tableJoinDao).delete(rowJoin);
        verify(contentDao).deleteByParent(TABLE_JOIN_ID);
    }
}