package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListItemCloneServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CLONE_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private TableCloneService tableCloneService;

    @Mock
    private DefaultListItemCloneService defaultListItemCloneService;

    @Mock
    private ChecklistCloneService checklistCloneService;

    @Mock
    private FileCloneService cloneFileService;

    @InjectMocks
    private ListItemCloneService underTest;

    @Test
    void clone_text() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.TEXT, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(defaultListItemCloneService).should().clone(PARENT_ID, listItem);
        then(tableCloneService).shouldHaveNoInteractions();
        then(checklistCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_link() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.LINK, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(defaultListItemCloneService).should().clone(PARENT_ID, listItem);
        then(tableCloneService).shouldHaveNoInteractions();
        then(checklistCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_onlyTitle() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.ONLY_TITLE, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(defaultListItemCloneService).should().clone(PARENT_ID, listItem);
        then(tableCloneService).shouldHaveNoInteractions();
        then(checklistCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_checklist() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.CHECKLIST, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(checklistCloneService).should().clone(PARENT_ID, listItem);
        then(tableCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_table() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.TABLE, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(tableCloneService).should().cloneTable(PARENT_ID, listItem);
        then(checklistCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_checklistTable() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.CHECKLIST_TABLE, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(tableCloneService).should().cloneTable(PARENT_ID, listItem);
        then(checklistCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_customTable() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.CUSTOM_TABLE, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(tableCloneService).should().cloneTable(PARENT_ID, listItem);
        then(checklistCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_image() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.IMAGE, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(cloneFileService).should().cloneFile(PARENT_ID, listItem);
        then(tableCloneService).shouldHaveNoInteractions();
        then(checklistCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_file() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.FILE, PARENT_ID);
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(cloneFileService).should().cloneFile(PARENT_ID, listItem);
        then(tableCloneService).shouldHaveNoInteractions();
        then(checklistCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(listItemFactory).shouldHaveNoInteractions();
    }

    @Test
    void clone_category_noChildren() {
        ListItem listItem = createListItem(LIST_ITEM_ID, ListItemType.CATEGORY, PARENT_ID);
        ListItem listItemClone = createListItem(CLONE_ID, ListItemType.CATEGORY, PARENT_ID);

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItemFactory.clone(PARENT_ID, listItem)).willReturn(listItemClone);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(Collections.emptyList());

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(listItemFactory).should().clone(PARENT_ID, listItem);
        then(listItemDao).should().save(listItemClone);
        then(listItemDao).should().getByUserIdAndParent(USER_ID, LIST_ITEM_ID);
        then(tableCloneService).shouldHaveNoInteractions();
        then(checklistCloneService).shouldHaveNoInteractions();
        then(defaultListItemCloneService).shouldHaveNoInteractions();
        then(cloneFileService).shouldHaveNoInteractions();
    }

    @Test
    void clone_category_withChildren() {
        ListItem categoryItem = createListItem(LIST_ITEM_ID, ListItemType.CATEGORY, PARENT_ID);
        ListItem categoryClone = createListItem(CLONE_ID, ListItemType.CATEGORY, PARENT_ID);

        UUID childId = UUID.randomUUID();
        ListItem childTextItem = createListItem(childId, ListItemType.TEXT, LIST_ITEM_ID);

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(categoryItem);
        given(listItemFactory.clone(PARENT_ID, categoryItem)).willReturn(categoryClone);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of(childTextItem));

        underTest.clone(USER_ID, LIST_ITEM_ID);

        then(listItemFactory).should().clone(PARENT_ID, categoryItem);
        then(listItemDao).should().save(categoryClone);
        then(defaultListItemCloneService).should().clone(CLONE_ID, childTextItem);
    }

    private ListItem createListItem(UUID listItemId, ListItemType type, UUID parent) {
        return ListItem.builder()
            .listItemId(listItemId)
            .userId(USER_ID)
            .title("title-" + listItemId)
            .type(type)
            .parent(parent)
            .build();
    }
}