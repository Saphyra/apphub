package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
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
public class ChecklistTableCloneServiceTest {
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableCloneService tableCloneService;

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @Mock
    private CloneUtil cloneUtil;

    @InjectMocks
    private ChecklistTableCloneService underTest;

    @Mock
    private ListItem originalListItem;

    @Mock
    private ListItem listItemClone;

    @Mock
    private ChecklistTableRow originalChecklistTableRow;

    @Mock
    private ChecklistTableRow checklistTableRowClone;

    @Test
    public void cloneChecklistTable() {
        given(originalListItem.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(checklistTableRowDao.getByParent(ORIGINAL_LIST_ITEM_ID)).willReturn(Arrays.asList(originalChecklistTableRow));
        given(listItemClone.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(cloneUtil.clone(CLONED_LIST_ITEM_ID, originalChecklistTableRow)).willReturn(checklistTableRowClone);

        underTest.clone(originalListItem, listItemClone);

        verify(tableCloneService).clone(originalListItem, listItemClone);
        verify(checklistTableRowDao).save(checklistTableRowClone);
    }
}