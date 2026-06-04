package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final boolean CHECKED = true;
    private static final int INDEX = 0;
    private static final String CONTENT = "content";

    @Mock
    private ChecklistValidator checklistValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ChecklistItemFactory checklistItemFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private CommonListItemDao commonListItemDao;

    @InjectMocks
    private ChecklistCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Test
    void create() {
        ChecklistItemModel itemModel = ChecklistItemModel.builder()
            .checked(CHECKED)
            .index(INDEX)
            .content(CONTENT)
            .build();

        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .items(List.of(itemModel))
            .build();

        given(listItemFactory.create(USER_ID, PARENT, TITLE, ListItemType.CHECKLIST)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(checklistItemFactory.create(LIST_ITEM_ID, CHECKED, INDEX)).willReturn(checklistItem);
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(contentFactory.create(LIST_ITEM_ID, CHECKLIST_ITEM_ID, CONTENT)).willReturn(content);

        UUID result = underTest.create(USER_ID, request);

        then(checklistValidator).should().validate(USER_ID, request);
        then(commonListItemDao).should().saveChecklist(listItem, List.of(checklistItem), List.of(content));
        assertThat(result).isEqualTo(LIST_ITEM_ID);
    }
}