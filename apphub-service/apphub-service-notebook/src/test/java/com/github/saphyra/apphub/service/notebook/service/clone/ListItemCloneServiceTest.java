package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListItemCloneServiceTest {
    private static final UUID PARENT_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PARENT_LIST_ITEM_TITLE = "parent-list-item-title";
    private static final UUID PARENT_OF_PARENT = UUID.randomUUID();
    private static final UUID CATEGORY_LIST_ITEM_ID = UUID.randomUUID();
    private static final String CATEGORY_LIST_ITEM_TITLE = "category-list-item-title";
    private static final UUID PARENT_LIST_ITEM_CLONE_ID = UUID.randomUUID();
    private static final UUID LINK_LIST_ITEM_ID = UUID.randomUUID();
    private static final String LINK_LIST_ITEM_TITLE = "link-list-item-title";
    private static final UUID TEXT_LIST_ITEM_ID = UUID.randomUUID();
    private static final String TEXT_LIST_ITEM_TITLE = "text-list-item-title";
    private static final UUID CHECKLIST_LIST_ITEM_ID = UUID.randomUUID();
    private static final String CHECKLIST_LIST_ITEM_TITLE = "checklist-list-item-title";
    private static final UUID TABLE_LIST_ITEM_ID = UUID.randomUUID();
    private static final String TABLE_LIST_ITEM_TITLE = "table-list-item-title";
    private static final UUID CHECKLIST_TABLE_LIST_ITEM_ID = UUID.randomUUID();
    private static final String CHECKLIST_TABLE_ITEM_TITLE = "checklist-table-list-item-id";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private TableCloneService tableCloneService;

    @Mock
    private TextAndLinkCloneService textAndLinkCloneService;

    @Mock
    private ChecklistCloneService checklistCloneService;

    @Mock
    private ChecklistTableCloneService checklistTableCloneService;

    @InjectMocks
    private ListItemCloneService underTest;

    private final ListItem parentListItem = createListItem(PARENT_LIST_ITEM_ID, PARENT_LIST_ITEM_TITLE, ListItemType.CATEGORY, PARENT_OF_PARENT);

    @Mock
    private ListItem parentListItemClone;

    private final ListItem categoryListItem = createListItem(CATEGORY_LIST_ITEM_ID, CATEGORY_LIST_ITEM_TITLE, ListItemType.CATEGORY, PARENT_LIST_ITEM_ID);

    @Mock
    private ListItem categoryListItemClone;

    private final ListItem linkListItem = createListItem(LINK_LIST_ITEM_ID, LINK_LIST_ITEM_TITLE, ListItemType.LINK, PARENT_LIST_ITEM_ID);

    @Mock
    private ListItem linkListItemClone;

    private final ListItem textListItem = createListItem(TEXT_LIST_ITEM_ID, TEXT_LIST_ITEM_TITLE, ListItemType.TEXT, PARENT_LIST_ITEM_ID);

    @Mock
    private ListItem textListItemClone;

    private final ListItem checklistListItem = createListItem(CHECKLIST_LIST_ITEM_ID, CHECKLIST_LIST_ITEM_TITLE, ListItemType.CHECKLIST, PARENT_LIST_ITEM_ID);

    @Mock
    private ListItem checklistListItemClone;

    private final ListItem tableListItem = createListItem(TABLE_LIST_ITEM_ID, TABLE_LIST_ITEM_TITLE, ListItemType.TABLE, PARENT_LIST_ITEM_ID);

    @Mock
    private ListItem tableListItemClone;

    private final ListItem checklistTableListItem = createListItem(CHECKLIST_TABLE_LIST_ITEM_ID, CHECKLIST_TABLE_ITEM_TITLE, ListItemType.CHECKLIST_TABLE, PARENT_OF_PARENT);

    @Mock
    private ListItem checklistTableListItemClone;

    @Test
    public void cloneTest() {
        given(parentListItemClone.getListItemId()).willReturn(PARENT_LIST_ITEM_CLONE_ID);

        given(listItemDao.findByIdValidated(PARENT_LIST_ITEM_ID)).willReturn(parentListItem);
        given(listItemFactory.create(USER_ID, PARENT_LIST_ITEM_TITLE, PARENT_OF_PARENT, ListItemType.CATEGORY)).willReturn(parentListItemClone);

        given(listItemDao.getByUserIdAndParent(USER_ID, PARENT_LIST_ITEM_ID)).willReturn(Arrays.asList(categoryListItem, linkListItem, textListItem, checklistListItem, tableListItem, checklistTableListItem));

        given(listItemFactory.create(USER_ID, CATEGORY_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CATEGORY)).willReturn(categoryListItemClone);
        given(listItemDao.getByUserIdAndParent(USER_ID, CATEGORY_LIST_ITEM_ID)).willReturn(Collections.emptyList());

        given(listItemFactory.create(USER_ID, LINK_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.LINK)).willReturn(linkListItemClone);
        given(listItemFactory.create(USER_ID, TEXT_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.TEXT)).willReturn(textListItemClone);
        given(listItemFactory.create(USER_ID, CHECKLIST_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CHECKLIST)).willReturn(checklistListItemClone);
        given(listItemFactory.create(USER_ID, TABLE_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.TABLE)).willReturn(tableListItemClone);
        given(listItemFactory.create(USER_ID, CHECKLIST_TABLE_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CHECKLIST_TABLE)).willReturn(checklistTableListItemClone);

        underTest.clone(PARENT_LIST_ITEM_ID);

        verify(listItemDao).save(parentListItemClone);
        verify(listItemDao).save(categoryListItemClone);
        verify(listItemDao).save(linkListItemClone);
        verify(textAndLinkCloneService).clone(LINK_LIST_ITEM_ID, linkListItemClone);
        verify(listItemDao).save(textListItemClone);
        verify(textAndLinkCloneService).clone(TEXT_LIST_ITEM_ID, textListItemClone);
        verify(listItemDao).save(checklistListItemClone);
        verify(checklistCloneService).clone(checklistListItem, checklistListItemClone);
        verify(listItemDao).save(tableListItemClone);
        verify(tableCloneService).clone(tableListItem, tableListItemClone);
        verify(listItemDao).save(checklistTableListItemClone);
        verify(checklistTableCloneService).clone(checklistTableListItem, checklistTableListItemClone);
    }

    private ListItem createListItem(UUID listItemId, String title, ListItemType type, UUID parent) {
        return ListItem.builder()
            .listItemId(listItemId)
            .userId(USER_ID)
            .title(title)
            .type(type)
            .parent(parent)
            .build();
    }
}