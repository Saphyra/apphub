package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
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
public class TableDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final UUID TABLE_JOIN_ID = UUID.randomUUID();

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private TableDeletionService underTest;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableJoin tableJoin;

    @Test
    public void deleteByListItemId() {
        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableHead));
        given(tableHead.getTableHeadId()).willReturn(TABLE_HEAD_ID);

        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableJoin));
        given(tableJoin.getTableJoinId()).willReturn(TABLE_JOIN_ID);

        underTest.deleteByListItemId(LIST_ITEM_ID);

        verify(contentDao).deleteByParent(TABLE_HEAD_ID);
        verify(contentDao).deleteByParent(TABLE_JOIN_ID);
        verify(tableHeadDao).delete(tableHead);
        verify(tableJoinDao).delete(tableJoin);
    }
}