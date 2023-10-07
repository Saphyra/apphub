package com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.edition;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditChecklistItemSaveServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemFactory checklistItemFactory;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private EditChecklistItemSaveService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItemNodeRequest nodeRequest;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Test
    public void saveNewItems() {
        List<ChecklistItemNodeRequest> requests = Arrays.asList(
            ChecklistItemNodeRequest.builder().checklistItemId(CHECKLIST_ITEM_ID).build(),
            nodeRequest
        );

        BiWrapper<ChecklistItem, Content> nodeContentWrapper = new BiWrapper<>(checklistItem, content);

        given(checklistItemFactory.create(listItem, nodeRequest)).willReturn(nodeContentWrapper);

        underTest.saveNewItems(requests, listItem);

        verify(contentDao).save(content);
        verify(checklistItemDao).save(checklistItem);
    }
}