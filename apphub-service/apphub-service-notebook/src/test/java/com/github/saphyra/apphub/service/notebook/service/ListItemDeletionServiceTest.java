package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListItemDeletionServiceTest {
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ListItemDeletionService underTest;

    @Mock
    private ListItem deleted;

    @Mock
    private ListItem child;

    @Test
    public void deleteCategory() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(deleted);
        given(listItemDao.getByParent(LIST_ITEM_ID_1)).willReturn(Arrays.asList(child));

        given(deleted.getType()).willReturn(ListItemType.CATEGORY);
        given(deleted.getListItemId()).willReturn(LIST_ITEM_ID_1);
        given(child.getType()).willReturn(ListItemType.CATEGORY);
        given(child.getListItemId()).willReturn(LIST_ITEM_ID_2);

        underTest.deleteListItem(LIST_ITEM_ID_1, USER_ID);

        verify(listItemDao).delete(deleted);
        verify(listItemDao).delete(child);
        verify(listItemDao).getByParent(LIST_ITEM_ID_1);
        verify(listItemDao).getByParent(LIST_ITEM_ID_2);
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
}