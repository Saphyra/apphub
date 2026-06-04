package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChecklistItemCrudServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT = "content";
    private static final int INDEX = 3;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistItemFactory checklistItemFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemCrudService underTest;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Test
    void addChecklistItem_contentNull() {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .index(INDEX)
            .content(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.addChecklistItem(USER_ID, LIST_ITEM_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "content", "must not be null");
    }

    @Test
    void addChecklistItem_indexNull() {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .index(null)
            .content(CONTENT)
            .build();

        Throwable ex = catchThrowable(() -> underTest.addChecklistItem(USER_ID, LIST_ITEM_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "index", "must not be null");
    }

    @Test
    void addChecklistItem() {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .index(INDEX)
            .content(CONTENT)
            .build();

        List<Content> contents = new ArrayList<>();
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(contents);
        given(checklistItemFactory.create(LIST_ITEM_ID, false, INDEX)).willReturn(checklistItem);
        given(checklistItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(contentFactory.create(LIST_ITEM_ID, CHECKLIST_ITEM_ID, CONTENT)).willReturn(content);
        given(checklistItem.getListItemId()).willReturn(LIST_ITEM_ID);

        underTest.addChecklistItem(USER_ID, LIST_ITEM_ID, request);

        then(listItemDao).should().findByIdValidated(USER_ID, LIST_ITEM_ID);
        then(checklistItemDao).should().save(checklistItem);
        then(contentDao).should().save(LIST_ITEM_ID, List.of(content));
    }

    @Test
    void updateContent_contentNull() {
        Throwable ex = catchThrowable(() -> underTest.updateContent(LIST_ITEM_ID, CHECKLIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "content", "must not be null");
    }

    @Test
    void updateContent_contentNotFound() {
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(new ArrayList<>());

        ExceptionValidator.validateNotFoundException(() -> underTest.updateContent(LIST_ITEM_ID, CHECKLIST_ITEM_ID, CONTENT));
    }

    @Test
    void updateContent() {
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(content));
        given(content.contains(CHECKLIST_ITEM_ID)).willReturn(true);

        underTest.updateContent(LIST_ITEM_ID, CHECKLIST_ITEM_ID, CONTENT);

        then(content).should().add(CHECKLIST_ITEM_ID, CONTENT);
        then(contentDao).should().save(LIST_ITEM_ID, List.of(content));
    }

    @Test
    void deleteChecklistItem() {
        underTest.deleteChecklistItem(USER_ID, LIST_ITEM_ID, CHECKLIST_ITEM_ID);

        then(listItemDao).should().findByIdValidated(USER_ID, LIST_ITEM_ID);
        then(checklistItemDao).should().delete(LIST_ITEM_ID, CHECKLIST_ITEM_ID);
        then(contentDao).should().delete(LIST_ITEM_ID, CHECKLIST_ITEM_ID);
    }

    @Test
    void updateStatus_statusNull() {
        Throwable ex = catchThrowable(() -> underTest.updateStatus(LIST_ITEM_ID, CHECKLIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "status", "must not be null");
    }

    @Test
    void updateStatus() {
        given(checklistItemDao.findByIdValidated(LIST_ITEM_ID, CHECKLIST_ITEM_ID)).willReturn(checklistItem);

        underTest.updateStatus(LIST_ITEM_ID, CHECKLIST_ITEM_ID, true);

        then(checklistItem).should().setChecked(true);
        then(checklistItemDao).should().save(checklistItem);
    }

    @Test
    void deleteCheckedItems() {
        ChecklistItem checkedItem = checklistItem;
        ChecklistItem uncheckedItem = mock(ChecklistItem.class);

        given(checklistItemDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(checkedItem, uncheckedItem));
        given(checkedItem.isChecked()).willReturn(true);
        given(uncheckedItem.isChecked()).willReturn(false);
        given(checkedItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);

        underTest.deleteCheckedItems(LIST_ITEM_ID);

        then(checklistItemDao).should().delete(List.of(checkedItem));
        then(contentDao).should().deleteKeys(LIST_ITEM_ID, List.of(CHECKLIST_ITEM_ID));
    }
}