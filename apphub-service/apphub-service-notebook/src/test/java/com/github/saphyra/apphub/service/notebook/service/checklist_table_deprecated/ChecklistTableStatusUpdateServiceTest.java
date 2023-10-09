package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistTableStatusUpdateServiceTest {
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @InjectMocks
    private ChecklistTableStatusUpdateService underTest;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Test
    public void rowNotFound() {
        given(checklistTableRowDao.findById(ROW_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.updateStatus(ROW_ID, true));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    @Test
    public void updateStatus() {
        given(checklistTableRowDao.findById(ROW_ID)).willReturn(Optional.of(checklistTableRow));

        underTest.updateStatus(ROW_ID, true);

        verify(checklistTableRow).setChecked(true);
        verify(checklistTableRowDao).save(checklistTableRow);
    }
}