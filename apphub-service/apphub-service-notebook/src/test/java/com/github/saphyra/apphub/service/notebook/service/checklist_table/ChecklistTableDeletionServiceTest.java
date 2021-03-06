package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableDeletionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistTableDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private  TableDeletionService tableDeletionService;

    @Mock
    private  ChecklistTableRowDao checklistTableRowDao;

    @InjectMocks
    private ChecklistTableDeletionService underTest;

    @Test
    public void delete(){
        underTest.deleteByListItemId(LIST_ITEM_ID);

        verify(tableDeletionService).deleteByListItemId(LIST_ITEM_ID);;
        verify(checklistTableRowDao).deleteByParent(LIST_ITEM_ID);;
    }
}