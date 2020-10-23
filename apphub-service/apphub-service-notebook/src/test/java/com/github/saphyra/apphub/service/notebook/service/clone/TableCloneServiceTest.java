package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
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
public class TableCloneServiceTest {
    private static final UUID ORIGINAL_LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private TableHeadCloneService tableHeadCloneService;

    @Mock
    private TableJoinCloneService tableJoinCloneService;

    @InjectMocks
    private TableCloneService underTest;

    @Mock
    private ListItem original;

    @Mock
    private ListItem clone;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableJoin tableJoin;

    @Test
    public void cloneTest() {
        given(original.getListItemId()).willReturn(ORIGINAL_LIST_ITEM_ID);
        given(tableHeadDao.getByParent(ORIGINAL_LIST_ITEM_ID)).willReturn(Arrays.asList(tableHead));
        given(tableJoinDao.getByParent(ORIGINAL_LIST_ITEM_ID)).willReturn(Arrays.asList(tableJoin));

        underTest.clone(original, clone);

        verify(tableHeadCloneService).clone(clone, tableHead);
        verify(tableJoinCloneService).clone(clone, tableJoin);
    }
}