package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TableHeadCloneServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_TABLE_HEAD_ID = UUID.randomUUID();
    private static final UUID CLONED_TABLE_HEAD_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private CloneUtil cloneUtil;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private TableHeadDao tableHeadDao;

    @InjectMocks
    private TableHeadCloneService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead originalTableHead;

    @Mock
    private TableHead clonedTableHead;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Test
    public void cloneTest() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(originalTableHead.getTableHeadId()).willReturn(ORIGINAL_TABLE_HEAD_ID);
        given(clonedTableHead.getTableHeadId()).willReturn(CLONED_TABLE_HEAD_ID);
        given(originalContent.getContent()).willReturn(CONTENT);

        given(cloneUtil.clone(LIST_ITEM_ID, originalTableHead)).willReturn(clonedTableHead);

        given(contentDao.findByParentValidated(ORIGINAL_TABLE_HEAD_ID)).willReturn(originalContent);
        given(contentFactory.create(CLONED_TABLE_HEAD_ID, USER_ID, CONTENT)).willReturn(clonedContent);

        underTest.clone(listItem, originalTableHead);

        verify(tableHeadDao).save(clonedTableHead);
        verify(contentDao).save(clonedContent);
    }
}