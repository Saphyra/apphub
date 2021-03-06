package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.UnprocessableEntityException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableRowFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConvertTableToChecklistTableServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @Mock
    private ChecklistTableRowFactory checklistTableRowFactory;

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @InjectMocks
    private ConvertTableToChecklistTableService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Mock
    private TableJoin tableJoin;

    @Test
    public void notTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TEXT);

        Throwable ex = catchThrowable(() -> underTest.convert(LIST_ITEM_ID));

        assertThat(ex).isInstanceOf(UnprocessableEntityException.class);
        UnprocessableEntityException exception = (UnprocessableEntityException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
    }

    @Test
    public void convert() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TABLE);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(checklistTableRowFactory.create(USER_ID, LIST_ITEM_ID, 0, false)).willReturn(checklistTableRow);
        given(tableJoinDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(tableJoin));
        given(tableJoin.getRowIndex()).willReturn(0);

        underTest.convert(LIST_ITEM_ID);

        verify(listItem).setType(ListItemType.CHECKLIST_TABLE);
        verify(listItemDao).save(listItem);
        verify(checklistTableRowDao).save(checklistTableRow);
    }
}