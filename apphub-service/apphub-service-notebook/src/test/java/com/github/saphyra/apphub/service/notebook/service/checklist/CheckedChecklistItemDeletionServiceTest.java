package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CheckedChecklistItemDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private CheckedChecklistItemDeletionService underTest;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    public void deleteCheckedItems() {
        given(checklistItemDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistItem, checklistItem));
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItem.getChecked()).willReturn(true)
            .willReturn(false);

        underTest.deleteCheckedItems(LIST_ITEM_ID);

        verify(checklistItemDao, times(1)).delete(checklistItem);
        verify(contentDao, times(1)).deleteByParent(CHECKLIST_ITEM_ID);
    }
}