package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.clone.table.TableCloneService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
    private static final UUID ONLY_TITLE_LIST_ITEM_ID = UUID.randomUUID();
    private static final String ONLY_TITLE_TITLE = "only-title-title";
    private static final UUID IMAGE_LIST_ITEM_ID = UUID.randomUUID();
    private static final String IMAGE_TITLE = "image-title";
    private static final UUID FILE_LIST_ITEM_ID = UUID.randomUUID();
    private static final String FILE_TITLE = "file-title";
    private static final boolean PINNED = false;
    private static final boolean ARCHIVED = false;
    private static final UUID CUSTOM_TABLE_LIST_ITEM_ID = UUID.randomUUID();
    private static final String CUSTOM_TABLE_TITLE = "custom-table-title";

    @Mock
    private DeprecatedListItemDao listItemDao;

    @Mock
    private DeprecatedListItemFactory listItemFactory;

    @Mock
    private TableCloneService tableCloneService;

    @Mock
    private TextAndLinkCloneService textAndLinkCloneService;

    @Mock
    private ChecklistCloneService checklistCloneService;

    @Mock
    private FileCloneService cloneFileService;

    @InjectMocks
    private ListItemCloneService underTest;

    private final DeprecatedListItem parentListItem = createListItem(PARENT_LIST_ITEM_ID, PARENT_LIST_ITEM_TITLE, ListItemType.CATEGORY, PARENT_OF_PARENT);

    @Mock
    private DeprecatedListItem parentListItemClone;

    private final DeprecatedListItem categoryListItem = createListItem(CATEGORY_LIST_ITEM_ID, CATEGORY_LIST_ITEM_TITLE, ListItemType.CATEGORY, PARENT_LIST_ITEM_ID);

    @Mock
    private DeprecatedListItem categoryListItemClone;

    private final DeprecatedListItem linkListItem = createListItem(LINK_LIST_ITEM_ID, LINK_LIST_ITEM_TITLE, ListItemType.LINK, PARENT_LIST_ITEM_ID);

    @Mock
    private DeprecatedListItem linkListItemClone;

    private final DeprecatedListItem textListItem = createListItem(TEXT_LIST_ITEM_ID, TEXT_LIST_ITEM_TITLE, ListItemType.TEXT, PARENT_LIST_ITEM_ID);

    @Mock
    private DeprecatedListItem textListItemClone;

    private final DeprecatedListItem checklistListItem = createListItem(CHECKLIST_LIST_ITEM_ID, CHECKLIST_LIST_ITEM_TITLE, ListItemType.CHECKLIST, PARENT_LIST_ITEM_ID);

    @Mock
    private DeprecatedListItem checklistListItemClone;

    private final DeprecatedListItem tableListItem = createListItem(TABLE_LIST_ITEM_ID, TABLE_LIST_ITEM_TITLE, ListItemType.TABLE, PARENT_LIST_ITEM_ID);

    @Mock
    private DeprecatedListItem tableListItemClone;

    private final DeprecatedListItem checklistTableListItem = createListItem(CHECKLIST_TABLE_LIST_ITEM_ID, CHECKLIST_TABLE_ITEM_TITLE, ListItemType.CHECKLIST_TABLE, PARENT_OF_PARENT);

    @Mock
    private DeprecatedListItem checklistTableListItemClone;

    private final DeprecatedListItem onlyTitleListItem = createListItem(ONLY_TITLE_LIST_ITEM_ID, ONLY_TITLE_TITLE, ListItemType.ONLY_TITLE, PARENT_OF_PARENT);

    @Mock
    private DeprecatedListItem onlyTitleListItemClone;

    private final DeprecatedListItem imageListItem = createListItem(IMAGE_LIST_ITEM_ID, IMAGE_TITLE, ListItemType.IMAGE, PARENT_OF_PARENT);

    @Mock
    private DeprecatedListItem imageListItemClone;

    private final DeprecatedListItem fileListItem = createListItem(FILE_LIST_ITEM_ID, FILE_TITLE, ListItemType.FILE, PARENT_OF_PARENT);

    @Mock
    private DeprecatedListItem fileListItemClone;

    @Mock
    private DeprecatedListItem customTableListItemClone;

    private final DeprecatedListItem customTableListItem = createListItem(CUSTOM_TABLE_LIST_ITEM_ID, CUSTOM_TABLE_TITLE, ListItemType.CUSTOM_TABLE, PARENT_OF_PARENT);

    @Test
    public void cloneTest() {
        given(parentListItemClone.getListItemId()).willReturn(PARENT_LIST_ITEM_CLONE_ID);

        given(listItemDao.findByIdValidated(PARENT_LIST_ITEM_ID)).willReturn(parentListItem);
        given(listItemFactory.create(USER_ID, PARENT_LIST_ITEM_TITLE, PARENT_OF_PARENT, ListItemType.CATEGORY, PINNED, ARCHIVED)).willReturn(parentListItemClone);

        given(listItemDao.getByUserIdAndParent(USER_ID, PARENT_LIST_ITEM_ID)).willReturn(Arrays.asList(
            categoryListItem,
            linkListItem,
            textListItem,
            checklistListItem,
            tableListItem,
            checklistTableListItem,
            onlyTitleListItem,
            imageListItem,
            fileListItem,
            customTableListItem
        ));

        given(listItemFactory.create(USER_ID, CATEGORY_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CATEGORY, PINNED, ARCHIVED)).willReturn(categoryListItemClone);
        given(listItemDao.getByUserIdAndParent(USER_ID, CATEGORY_LIST_ITEM_ID)).willReturn(Collections.emptyList());

        given(listItemFactory.create(USER_ID, LINK_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.LINK, PINNED, ARCHIVED)).willReturn(linkListItemClone);
        given(listItemFactory.create(USER_ID, TEXT_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.TEXT, PINNED, ARCHIVED)).willReturn(textListItemClone);
        given(listItemFactory.create(USER_ID, CHECKLIST_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CHECKLIST, PINNED, ARCHIVED)).willReturn(checklistListItemClone);
        given(listItemFactory.create(USER_ID, TABLE_LIST_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.TABLE, PINNED, ARCHIVED)).willReturn(tableListItemClone);
        given(listItemFactory.create(USER_ID, CHECKLIST_TABLE_ITEM_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CHECKLIST_TABLE, PINNED, ARCHIVED)).willReturn(checklistTableListItemClone);
        given(listItemFactory.create(USER_ID, ONLY_TITLE_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.ONLY_TITLE, PINNED, ARCHIVED)).willReturn(onlyTitleListItemClone);
        given(listItemFactory.create(USER_ID, IMAGE_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.IMAGE, PINNED, ARCHIVED)).willReturn(imageListItemClone);
        given(listItemFactory.create(USER_ID, FILE_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.FILE, PINNED, ARCHIVED)).willReturn(fileListItemClone);
        given(listItemFactory.create(USER_ID, CUSTOM_TABLE_TITLE, PARENT_LIST_ITEM_CLONE_ID, ListItemType.CUSTOM_TABLE, PINNED, ARCHIVED)).willReturn(customTableListItemClone);

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
        verify(tableCloneService).cloneTable(tableListItem, tableListItemClone);
        verify(tableCloneService).cloneTable(customTableListItem, customTableListItemClone);
        verify(listItemDao).save(checklistTableListItemClone);
        verify(listItemDao).save(onlyTitleListItemClone);
        verify(cloneFileService).cloneFile(imageListItem, imageListItemClone);
        verify(listItemDao).save(imageListItemClone);
        verify(cloneFileService).cloneFile(fileListItem, fileListItemClone);
        verify(listItemDao).save(fileListItemClone);
        verify(listItemDao).save(checklistTableListItemClone);
        verify(listItemDao).save(customTableListItemClone);
    }

    private DeprecatedListItem createListItem(UUID listItemId, String title, ListItemType type, UUID parent) {
        return DeprecatedListItem.builder()
            .listItemId(listItemId)
            .userId(USER_ID)
            .title(title)
            .type(type)
            .parent(parent)
            .build();
    }
}