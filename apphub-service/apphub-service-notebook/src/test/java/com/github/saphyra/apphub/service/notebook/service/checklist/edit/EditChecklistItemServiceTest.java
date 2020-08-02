package com.github.saphyra.apphub.service.notebook.service.checklist.edit;


import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.NodeContentWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditChecklistItemServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemNodeRequestValidator checklistItemNodeRequestValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private EditChecklistItemDeletionService editChecklistItemDeletionService;

    @Mock
    private EditChecklistItemUpdateService editChecklistItemUpdateService;

    @Mock
    private EditChecklistItemSaveService editChecklistItemSaveService;

    @InjectMocks
    private EditChecklistItemService underTest;

    @Mock
    private ChecklistItemNodeRequest request;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Captor
    private ArgumentCaptor<Map<UUID, NodeContentWrapper>> captor1;

    @Captor
    private ArgumentCaptor<Map<UUID, NodeContentWrapper>> captor2;

    @Test
    public void edit() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItemDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistItem));
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID)).willReturn(content);

        underTest.edit(Arrays.asList(request), LIST_ITEM_ID);

        verify(checklistItemNodeRequestValidator).validate(request);

        verify(editChecklistItemDeletionService).deleteItems(eq(Arrays.asList(request)), captor1.capture());
        assertThat(captor1.getValue().get(CHECKLIST_ITEM_ID).getContent()).isEqualTo(content);
        assertThat(captor1.getValue().get(CHECKLIST_ITEM_ID).getChecklistItem()).isEqualTo(checklistItem);

        verify(editChecklistItemUpdateService).updateItems(eq(Arrays.asList(request)), captor2.capture());
        assertThat(captor2.getValue().get(CHECKLIST_ITEM_ID).getContent()).isEqualTo(content);
        assertThat(captor2.getValue().get(CHECKLIST_ITEM_ID).getChecklistItem()).isEqualTo(checklistItem);

        verify(editChecklistItemSaveService).saveNewItems(Arrays.asList(request), listItem);
    }
}