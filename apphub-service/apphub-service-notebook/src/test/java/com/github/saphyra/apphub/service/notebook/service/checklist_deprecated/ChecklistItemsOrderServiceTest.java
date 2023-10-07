package com.github.saphyra.apphub.service.notebook.service.checklist_deprecated;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
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
public class ChecklistItemsOrderServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID_2 = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemsOrderService underTest;

    @Mock
    private ChecklistItem checklistItem1;

    @Mock
    private ChecklistItem checklistItem2;

    @Mock
    private Content content1;

    @Mock
    private Content content2;

    @Test
    public void orderChecklistItems() {
        given(checklistItemDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistItem1, checklistItem2));
        given(checklistItem1.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID_1);
        given(checklistItem2.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID_2);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID_1)).willReturn(content1);
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID_2)).willReturn(content2);

        given(content1.getContent()).willReturn("B");
        given(content2.getContent()).willReturn("A");

        underTest.orderChecklistItems(LIST_ITEM_ID);

        verify(checklistItem1).setOrder(1);
        verify(checklistItem2).setOrder(0);
        verify(checklistItemDao).saveAll(Arrays.asList(checklistItem2, checklistItem1));
    }
}