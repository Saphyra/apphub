package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ListItemDeletionServiceTest {
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ChecklistDeletionService checklistDeletionService;

    @Mock
    private TableDeletionService tableDeletionService;

    @Mock
    private FileDeletionService fileDeletionService;

    @InjectMocks
    private ListItemDeletionService underTest;

    @Mock
    private ListItem deleted;

    @Mock
    private ListItem child;

    @Test
    public void deleteCategory() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID_1)).willReturn(Arrays.asList(child));

        given(deleted.getType()).willReturn(ListItemType.CATEGORY);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(child.getType()).willReturn(ListItemType.CATEGORY);
        given(child.getListItemId()).willReturn(LIST_ITEM_ID_2);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(listItemDao).delete(child);
        verify(listItemDao).getByUserIdAndParent(USER_ID, LIST_ITEM_ID_1);
        verify(listItemDao).getByUserIdAndParent(USER_ID, LIST_ITEM_ID_2);
    }

    @Test
    public void deleteText() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(deleted.getType()).willReturn(ListItemType.TEXT);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(contentDao).deleteByParent(LIST_ITEM_ID_1);
    }

    @Test
    public void deleteLink() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(deleted.getType()).willReturn(ListItemType.LINK);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(contentDao).deleteByParent(LIST_ITEM_ID_1);
    }

    @Test
    public void deleteOnlyTitle() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getType()).willReturn(ListItemType.ONLY_TITLE);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
    }

    @Test
    public void deleteChecklistItem() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(deleted.getType()).willReturn(ListItemType.CHECKLIST);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(checklistDeletionService).delete(LIST_ITEM_ID_1);
    }

    @Test
    public void deleteTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getType()).willReturn(ListItemType.TABLE);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(tableDeletionService).delete(deleted);
    }

    @Test
    public void deleteChecklistTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getType()).willReturn(ListItemType.CHECKLIST_TABLE);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(tableDeletionService).delete(deleted);
    }

    @Test
    public void deleteImage() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(deleted.getType()).willReturn(ListItemType.IMAGE);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(fileDeletionService).deleteFile(LIST_ITEM_ID_1);
    }

    @Test
    public void deleteFile() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(deleted.getType()).willReturn(ListItemType.FILE);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(fileDeletionService).deleteFile(LIST_ITEM_ID_1);
    }

    @Test
    void deleteCustomTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(deleted.getType()).willReturn(ListItemType.CUSTOM_TABLE);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(tableDeletionService).delete(deleted);
    }
}