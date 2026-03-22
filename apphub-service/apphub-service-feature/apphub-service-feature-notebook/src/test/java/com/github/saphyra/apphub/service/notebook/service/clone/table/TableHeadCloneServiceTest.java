package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableHeadCloneServiceTest {
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CLONED_LIST_ITEM_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 42;
    private static final UUID ORIGINAL_TABLE_HEAD_ID = UUID.randomUUID();
    private static final UUID CLONED_TABLE_HEAD_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableHeadFactory tableHeadFactory;

    @InjectMocks
    private TableHeadCloneService underTest;

    @Mock
    private ListItem originalListItem;

    @Mock
    private ListItem listItemClone;

    @Mock
    private TableHead originalTableHead;

    @Mock
    private TableHead clonedTableHead;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Test
    void cloneTableHeads() {
        given(originalListItem.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(tableHeadDao.getByParent(ORIGINAL_LIST_ITEM_ID)).willReturn(List.of(originalTableHead));
        given(listItemClone.getUserId()).willReturn(USER_ID);
        given(listItemClone.getListItemId()).willReturn(CLONED_LIST_ITEM_ID);
        given(originalTableHead.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(tableHeadFactory.create(USER_ID, CLONED_LIST_ITEM_ID, COLUMN_INDEX)).willReturn(clonedTableHead);
        given(originalTableHead.getTableHeadId()).willReturn(ORIGINAL_TABLE_HEAD_ID);
        given(contentDao.findByParentValidated(ORIGINAL_TABLE_HEAD_ID)).willReturn(originalContent);
        given(clonedTableHead.getTableHeadId()).willReturn(CLONED_TABLE_HEAD_ID);
        given(originalContent.getContent()).willReturn(CONTENT);
        given(contentFactory.create(CLONED_LIST_ITEM_ID, CLONED_TABLE_HEAD_ID, USER_ID, CONTENT)).willReturn(clonedContent);

        underTest.cloneTableHeads(originalListItem, listItemClone);

        then(tableHeadDao).should().save(clonedTableHead);
        then(contentDao).should().save(clonedContent);
    }
}