package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistTableStatusUpdateServiceTest {
    private static final int ROW_INDEX = 134;
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @InjectMocks
    private ChecklistTableStatusUpdateService underTest;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Test
    public void rowNotFound() {
        given(checklistTableRowDao.findByParentAndRowIndex(LIST_ITEM_ID, ROW_INDEX)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.updateStatus(LIST_ITEM_ID, ROW_INDEX, true));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
    }

    @Test
    public void updateStatus() {
        given(checklistTableRowDao.findByParentAndRowIndex(LIST_ITEM_ID, ROW_INDEX)).willReturn(Optional.of(checklistTableRow));

        underTest.updateStatus(LIST_ITEM_ID, ROW_INDEX, true);

        verify(checklistTableRow).setChecked(true);
        verify(checklistTableRowDao).save(checklistTableRow);
    }
}