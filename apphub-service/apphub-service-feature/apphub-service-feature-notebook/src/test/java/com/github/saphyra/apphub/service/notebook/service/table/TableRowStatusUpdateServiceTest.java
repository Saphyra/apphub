package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableRowStatusUpdateServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private TableRowDao tableRowDao;

    @InjectMocks
    private TableRowStatusUpdateService underTest;

    @Mock
    private TableRow tableRow;

    @Test
    void setRowStatus_statusNull() {
        Throwable ex = catchThrowable(() -> underTest.setRowStatus(LIST_ITEM_ID, ROW_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "status", "must not be null");
    }

    @Test
    void setRowStatus() {
        given(tableRowDao.findByIdValidated(LIST_ITEM_ID, ROW_ID)).willReturn(tableRow);

        underTest.setRowStatus(LIST_ITEM_ID, ROW_ID, true);

        then(tableRowDao).should().findByIdValidated(LIST_ITEM_ID, ROW_ID);
        then(tableRow).should().setChecked(true);
        then(tableRowDao).should().save(tableRow);
    }
}