package com.github.saphyra.apphub.service.notebook.service.checklist.edition;

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

import java.util.*;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditChecklistItemUpdateServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final Integer NEW_ORDER = 8769876;
    private static final String NEW_CONTENT = "new-content";

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private EditChecklistItemUpdateService underTest;

    @Mock
    private Content content;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    public void updateItems() {
        Map<UUID, BiWrapper<ChecklistItem, Content>> actualItems = new HashMap<UUID, BiWrapper<ChecklistItem, Content>>() {{
            put(CHECKLIST_ITEM_ID, new BiWrapper<>(checklistItem, content));
        }};

        List<ChecklistItemNodeRequest> requests = Arrays.asList(
            new ChecklistItemNodeRequest(),
            ChecklistItemNodeRequest.builder().checklistItemId(CHECKLIST_ITEM_ID).order(NEW_ORDER).checked(true).content(NEW_CONTENT).build()
        );

        underTest.updateItems(requests, actualItems);

        verify(checklistItem).setOrder(NEW_ORDER);
        verify(checklistItem).setChecked(true);
        verify(content).setContent(NEW_CONTENT);
        verify(checklistItemDao).save(checklistItem);
        verify(contentDao).save(content);
    }
}