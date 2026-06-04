package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
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
class EditChecklistServiceTest {
    private static final String NEW_TITLE = "new-title";
    private static final UUID UNMODIFIED_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final Integer UNMODIFIED_INDEX = 0;
    private static final String UNMODIFIED_CONTENT = "unmodified-content";
    private static final UUID MODIFIED_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final Integer MODIFIED_INDEX = 1;
    private static final String MODIFIED_CONTENT = "modified-content";
    private static final Integer NEW_INDEX = 3;
    private static final String NEW_CONTENT = "new-content";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String ORIGINAL_TITLE = "original-title";
    private static final UUID DELETED_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final Integer BATCH_INDEX = 11;
    private static final String DELETED_CONTENT = "deleted-content";
    private static final UUID NEW_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String ORIGINAL_CONTENT = "original-content";

    @Mock
    private ChecklistValidator checklistValidator;

    @Mock
    private ChecklistQueryService checklistQueryService;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistItemFactory checklistItemFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private CommonListItemDao commonListItemDao;

    @InjectMocks
    private EditChecklistService underTest;

    @Mock
    private ChecklistResponse checklistResponse;

    @Mock
    private ChecklistItem newChecklistItem;

    @Mock
    private Content modifiedContent;

    @Mock
    private Content newContent;

    @Test
    void edit() {
        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(
                ChecklistItemModel.builder()
                    .type(ItemType.EXISTING)
                    .checklistItemId(UNMODIFIED_CHECKLIST_ITEM_ID)
                    .index(UNMODIFIED_INDEX)
                    .checked(false)
                    .content(UNMODIFIED_CONTENT)
                    .build(),
                ChecklistItemModel.builder()
                    .type(ItemType.EXISTING)
                    .checklistItemId(MODIFIED_CHECKLIST_ITEM_ID)
                    .index(MODIFIED_INDEX)
                    .checked(true)
                    .content(MODIFIED_CONTENT)
                    .build(),
                ChecklistItemModel.builder()
                    .type(ItemType.NEW)
                    .index(NEW_INDEX)
                    .checked(true)
                    .content(NEW_CONTENT)
                    .build()
            ))
            .build();

        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST)
            .title(ORIGINAL_TITLE)
            .build();
        ChecklistItem deletedChecklistItem = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(DELETED_CHECKLIST_ITEM_ID)
            .build();
        ChecklistItem modifiedChecklistItem = ChecklistItem.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(MODIFIED_CHECKLIST_ITEM_ID)
            .index(MODIFIED_INDEX)
            .checked(false)
            .build();
        Content existingContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(BATCH_INDEX)
            .build()
            .add(DELETED_CHECKLIST_ITEM_ID, DELETED_CONTENT)
            .add(UNMODIFIED_CHECKLIST_ITEM_ID, UNMODIFIED_CONTENT)
            .add(MODIFIED_CHECKLIST_ITEM_ID, ORIGINAL_CONTENT);
        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> checklist = new TriWrapper<>(
            listItem,
            List.of(
                deletedChecklistItem,
                ChecklistItem.builder()
                    .listItemId(LIST_ITEM_ID)
                    .checklistItemId(UNMODIFIED_CHECKLIST_ITEM_ID)
                    .index(UNMODIFIED_INDEX)
                    .checked(false)
                    .build(),
                modifiedChecklistItem
            ),
            List.of(
                existingContent
            )
        );
        given(commonListItemDao.findChecklistValidated(USER_ID, LIST_ITEM_ID)).willReturn(checklist);
        given(checklistQueryService.getChecklistResponse(USER_ID, LIST_ITEM_ID)).willReturn(checklistResponse);
        given(checklistItemFactory.create(LIST_ITEM_ID, true, NEW_INDEX)).willReturn(newChecklistItem);
        given(newChecklistItem.getChecklistItemId()).willReturn(NEW_CHECKLIST_ITEM_ID);
        given(contentFactory.create(LIST_ITEM_ID, MODIFIED_CHECKLIST_ITEM_ID, MODIFIED_CONTENT)).willReturn(modifiedContent);
        given(contentFactory.create(LIST_ITEM_ID, NEW_CHECKLIST_ITEM_ID, NEW_CONTENT)).willReturn(newContent);

        assertThat(underTest.edit(USER_ID, LIST_ITEM_ID, request)).isEqualTo(checklistResponse);

        then(checklistValidator).should().validate(request);
        then(listItemDao).should().save(listItem);
        then(commonListItemDao).should().editChecklist(LIST_ITEM_ID, List.of(deletedChecklistItem), List.of(newChecklistItem), List.of(modifiedChecklistItem), List.of(existingContent, newContent, modifiedContent));
    }
}