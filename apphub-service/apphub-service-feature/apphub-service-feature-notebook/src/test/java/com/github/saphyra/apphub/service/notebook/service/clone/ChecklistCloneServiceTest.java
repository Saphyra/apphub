package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistCloneServiceTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CLONED_CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT_VALUE = "content-value";

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ChecklistItemFactory checklistItemFactory;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ChecklistItemDao checklistItemDao;

    @InjectMocks
    private ChecklistCloneService underTest;

    @Mock
    private ListItem toClone;

    @Mock
    private ListItem clonedListItem;

    @Mock
    private ChecklistItem originalItem;

    @Mock
    private ChecklistItem clonedItem;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Test
    void clone_contentNotFound() {
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(checklistItemDao.getByListItemId(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalItem));
        given(contentDao.getByListItemId(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalContent));
        given(originalItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItemFactory.clone(CLONED_LIST_ITEM_ID, originalItem)).willReturn(clonedItem);
        given(originalContent.contains(CHECKLIST_ITEM_ID)).willReturn(false);

        ExceptionValidator.validateLoggedException(() -> underTest.clone(PARENT, toClone), HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void cloneSuccessfully() {
        given(toClone.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(listItemFactory.clone(PARENT, toClone)).willReturn(clonedListItem);
        given(clonedListItem.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(checklistItemDao.getByListItemId(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalItem));
        given(contentDao.getByListItemId(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalContent));
        given(originalItem.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(checklistItemFactory.clone(CLONED_LIST_ITEM_ID, originalItem)).willReturn(clonedItem);
        given(originalContent.contains(CHECKLIST_ITEM_ID)).willReturn(true);
        given(originalContent.get(CHECKLIST_ITEM_ID)).willReturn(CONTENT_VALUE);
        given(clonedItem.getChecklistItemId()).willReturn(CLONED_CHECKLIST_ITEM_ID);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_CHECKLIST_ITEM_ID, CONTENT_VALUE)).willReturn(clonedContent);

        underTest.clone(PARENT, toClone);

        then(commonListItemDao).should().saveChecklist(clonedListItem, List.of(clonedItem), List.of(clonedContent));
    }
}