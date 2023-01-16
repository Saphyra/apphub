package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TableJoinCloneServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_TABLE_JOIN_ID = UUID.randomUUID();
    private static final UUID CLONED_TABLE_JOIN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private CloneUtil cloneUtil;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private TableJoinDao tableJoinDao;

    @InjectMocks
    private TableJoinCloneService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableJoin originalTableJoin;

    @Mock
    private TableJoin clonedTableJoin;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Test
    public void cloneTest() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(originalTableJoin.getTableJoinId()).willReturn(ORIGINAL_TABLE_JOIN_ID);
        given(clonedTableJoin.getTableJoinId()).willReturn(CLONED_TABLE_JOIN_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(originalContent.getContent()).willReturn(CONTENT);

        given(cloneUtil.clone(LIST_ITEM_ID, originalTableJoin)).willReturn(clonedTableJoin);
        given(contentDao.findByParentValidated(ORIGINAL_TABLE_JOIN_ID)).willReturn(originalContent);
        given(contentFactory.create(LIST_ITEM_ID, CLONED_TABLE_JOIN_ID, USER_ID, CONTENT)).willReturn(clonedContent);

        underTest.clone(listItem, originalTableJoin);

        verify(tableJoinDao).save(clonedTableJoin);
        verify(contentDao).save(clonedContent);
    }
}