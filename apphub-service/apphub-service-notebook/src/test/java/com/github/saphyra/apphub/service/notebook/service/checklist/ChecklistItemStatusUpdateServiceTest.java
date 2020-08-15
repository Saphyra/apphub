package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistItemStatusUpdateServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @InjectMocks
    private ChecklistItemStatusUpdateService underTest;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    public void update() {
        given(checklistItemDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checklistItem);

        underTest.update(CHECKLIST_ITEM_ID, true);

        verify(checklistItem).setChecked(true);
        verify(checklistItemDao).save(checklistItem);
    }
}