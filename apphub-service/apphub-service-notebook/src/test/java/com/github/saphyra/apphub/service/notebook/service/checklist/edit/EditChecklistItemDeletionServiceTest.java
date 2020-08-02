package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.NodeContentWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditChecklistItemDeletionServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private EditChecklistItemDeletionService underTest;

    @Mock
    private ChecklistItem checklistItem1;

    @Mock
    private ChecklistItem checklistItem2;

    @Mock
    private Content content1;

    @Mock
    private Content content2;

    @Test
    public void deleteItems() {
        List<ChecklistItemNodeRequest> requests = Arrays.asList(
            new ChecklistItemNodeRequest(),
            ChecklistItemNodeRequest.builder().checklistItemId(CHECKLIST_ITEM_ID).build()
        );

        Map<UUID, NodeContentWrapper> actualItems = new HashMap<UUID, NodeContentWrapper>() {{
            put(CHECKLIST_ITEM_ID, NodeContentWrapper.builder().content(content1).checklistItem(checklistItem1).build());
            put(UUID.randomUUID(), NodeContentWrapper.builder().checklistItem(checklistItem2).content(content2).build());
        }};

        underTest.deleteItems(requests, actualItems);

        verify(checklistItemDao).delete(checklistItem2);
        verify(contentDao).delete(content2);
    }
}