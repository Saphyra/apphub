package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistItemDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemDeletionService underTest;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    void delete() {
        given(checklistItemDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checklistItem);
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);

        underTest.delete(CHECKLIST_ITEM_ID);

        then(contentDao).should().deleteByParent(CHECKLIST_ITEM_ID);
        then(checklistItemDao).should().delete(checklistItem);
    }

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