package com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.edition;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

        Map<UUID, BiWrapper<ChecklistItem, Content>> actualItems = new HashMap<UUID, BiWrapper<ChecklistItem, Content>>() {{
            put(CHECKLIST_ITEM_ID, new BiWrapper<>(checklistItem1, content1));
            put(UUID.randomUUID(), new BiWrapper<>(checklistItem2, content2));
        }};

        underTest.deleteItems(requests, actualItems);

        verify(checklistItemDao).delete(checklistItem2);
        verify(contentDao).delete(content2);
    }
}