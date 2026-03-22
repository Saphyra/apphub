package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableRowStatusUpdateServiceTest {
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private CheckedItemDao checkedItemDao;

    @InjectMocks
    private TableRowStatusUpdateService underTest;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void setRowStatus() {
        given(checkedItemDao.findByIdValidated(ROW_ID)).willReturn(checkedItem);

        underTest.setRowStatus(ROW_ID, true);

        then(checkedItem).should().setChecked(true);
        then(checkedItemDao).should().save(checkedItem);
    }
}