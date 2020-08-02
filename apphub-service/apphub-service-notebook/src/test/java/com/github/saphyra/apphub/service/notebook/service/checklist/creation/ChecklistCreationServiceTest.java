package com.github.saphyra.apphub.service.notebook.service.checklist.creation;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.service.checklist.NodeContentWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistCreationServiceTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String CONTENT = "content";
    private static final Integer ORDER = 253;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemFactory checklistItemFactory;

    @Mock
    private CreateChecklistItemRequestValidator createChecklistItemRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @InjectMocks
    private ChecklistCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Content content;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    public void create() {
        ChecklistItemNodeRequest nodeRequest = ChecklistItemNodeRequest.builder()
            .content(CONTENT)
            .checked(true)
            .order(ORDER)
            .build();
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(Arrays.asList(nodeRequest))
            .build();

        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.CHECKLIST)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        NodeContentWrapper nodeContentWrapper = NodeContentWrapper.builder()
            .checklistItem(checklistItem)
            .content(content)
            .build();
        given(checklistItemFactory.create(listItem, nodeRequest)).willReturn(nodeContentWrapper);

        UUID result = underTest.create(request, USER_ID);

        verify(createChecklistItemRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        assertThat(result).isEqualTo(LIST_ITEM_ID);
        verify(contentDao).save(content);
        verify(checklistItemDao).save(checklistItem);
    }
}