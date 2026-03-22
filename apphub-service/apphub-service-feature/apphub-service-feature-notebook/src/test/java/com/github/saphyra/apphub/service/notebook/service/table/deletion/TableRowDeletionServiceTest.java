package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableRowDeletionServiceTest {
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private TableColumnDeletionService tableColumnDeletionService;

    @Mock
    private CheckedItemDao checkedItemDao;

    @InjectMocks
    private TableRowDeletionService underTest;

    @Mock
    private Dimension row;

    @Test
    void deleteRow() {
        given(row.getDimensionId()).willReturn(ROW_ID);

        underTest.deleteRow(row);

        then(tableColumnDeletionService).should().deleteColumnsOfRow(ROW_ID);
        then(checkedItemDao).should().deleteById(ROW_ID);
        then(dimensionDao).should().delete(row);
    }
}