package com.github.saphyra.apphub.service.notebook.service.checklist.edition;


import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.TitleValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditChecklistItemServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";

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

    @Mock
    private TitleValidator titleValidator;

    @InjectMocks
    private EditChecklistItemService underTest;

    @Mock
    private ChecklistItemNodeRequest node;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Captor
    private ArgumentCaptor<Map<UUID, BiWrapper<ChecklistItem, Content>>> captor1;

    @Captor
    private ArgumentCaptor<Map<UUID, BiWrapper<ChecklistItem, Content>>> captor2;

    @Test
    public void edit() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItemDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistItem));
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID)).willReturn(content);

        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(node))
            .build();

        underTest.edit(request, LIST_ITEM_ID);

        verify(titleValidator).validate(TITLE);
        verify(checklistItemNodeRequestValidator).validate(node);

        verify(listItem).setTitle(TITLE);
        verify(listItemDao).save(listItem);

        verify(editChecklistItemDeletionService).deleteItems(eq(Arrays.asList(node)), captor1.capture());
        assertThat(captor1.getValue().get(CHECKLIST_ITEM_ID).getEntity2()).isEqualTo(content);
        assertThat(captor1.getValue().get(CHECKLIST_ITEM_ID).getEntity1()).isEqualTo(checklistItem);

        verify(editChecklistItemUpdateService).updateItems(eq(Arrays.asList(node)), captor2.capture());
        assertThat(captor2.getValue().get(CHECKLIST_ITEM_ID).getEntity2()).isEqualTo(content);
        assertThat(captor2.getValue().get(CHECKLIST_ITEM_ID).getEntity1()).isEqualTo(checklistItem);

        verify(editChecklistItemSaveService).saveNewItems(Arrays.asList(node), listItem);
    }
}