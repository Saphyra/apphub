package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderChecklistItemsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID_2 = UUID.randomUUID();

    @Mock
    private ChecklistQueryService checklistQueryService;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @InjectMocks
    private OrderChecklistItemsService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistResponse checklistResponse;

    @Test
    void orderItems_itemsAreReordered() {
        ChecklistItem item1 = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(CHECKLIST_ITEM_ID_1)
            .index(0)
            .checked(false)
            .build();
        ChecklistItem item2 = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(CHECKLIST_ITEM_ID_2)
            .index(1)
            .checked(false)
            .build();
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(new HashMap<>(Map.of(CHECKLIST_ITEM_ID_1, "B", CHECKLIST_ITEM_ID_2, "A")))
            .build();

        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> wrapper = new TriWrapper<>(
            listItem,
            List.of(item1, item2),
            List.of(content)
        );

        given(commonListItemDao.findChecklistValidated(USER_ID, LIST_ITEM_ID)).willReturn(wrapper);
        given(checklistQueryService.getChecklistResponse(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.orderItems(USER_ID, LIST_ITEM_ID);

        assertThat(item2.getIndex()).isEqualTo(0);
        assertThat(item1.getIndex()).isEqualTo(1);
        then(checklistItemDao).should().save(List.of(item2, item1));
        assertThat(result).isEqualTo(checklistResponse);
    }

    @Test
    void orderItems_alreadyInOrder() {
        ChecklistItem item1 = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(CHECKLIST_ITEM_ID_1)
            .index(0)
            .checked(false)
            .build();
        ChecklistItem item2 = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(CHECKLIST_ITEM_ID_2)
            .index(1)
            .checked(false)
            .build();
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(new HashMap<>(Map.of(CHECKLIST_ITEM_ID_1, "A", CHECKLIST_ITEM_ID_2, "B")))
            .build();

        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> wrapper = new TriWrapper<>(
            listItem,
            List.of(item1, item2),
            List.of(content)
        );

        given(commonListItemDao.findChecklistValidated(USER_ID, LIST_ITEM_ID)).willReturn(wrapper);
        given(checklistQueryService.getChecklistResponse(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.orderItems(USER_ID, LIST_ITEM_ID);

        assertThat(item1.getIndex()).isEqualTo(0);
        assertThat(item2.getIndex()).isEqualTo(1);
        then(checklistItemDao).should().save(List.of());
        assertThat(result).isEqualTo(checklistResponse);
    }
}