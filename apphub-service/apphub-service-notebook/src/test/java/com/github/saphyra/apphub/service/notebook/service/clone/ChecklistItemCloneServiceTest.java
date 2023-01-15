package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistItemCloneServiceTest {
    private static final UUID ORIGINAL_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CLONED_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ORIGINAL_CONTENT = "original-content";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private CloneUtil cloneUtil;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private ChecklistItemCloneService underTest;

    @Mock
    private ListItem clone;

    @Mock
    private ChecklistItem originalChecklistItem;

    @Mock
    private ChecklistItem clonedChecklistItem;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Test
    public void cloneTest() {
        given(clone.getListItemId()).willReturn(LIST_ITEM_ID);
        given(clone.getUserId()).willReturn(USER_ID);
        given(cloneUtil.clone(LIST_ITEM_ID, originalChecklistItem)).willReturn(clonedChecklistItem);

        given(originalChecklistItem.getChecklistItemId()).willReturn(ORIGINAL_CHECKLIST_ITEM_ID);
        given(contentDao.findByParentValidated(ORIGINAL_CHECKLIST_ITEM_ID)).willReturn(originalContent);

        given(clonedChecklistItem.getChecklistItemId()).willReturn(CLONED_CHECKLIST_ITEM_ID);
        given(originalContent.getContent()).willReturn(ORIGINAL_CONTENT);
        given(contentFactory.create(LIST_ITEM_ID, CLONED_CHECKLIST_ITEM_ID, USER_ID, ORIGINAL_CONTENT)).willReturn(clonedContent);

        underTest.clone(clone, originalChecklistItem);

        verify(checklistItemDao).save(clonedChecklistItem);
        verify(contentDao).save(clonedContent);
    }
}