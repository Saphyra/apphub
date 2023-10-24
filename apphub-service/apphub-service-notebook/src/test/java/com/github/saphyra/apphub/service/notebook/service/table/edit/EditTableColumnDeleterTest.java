package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableColumnDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableColumnDeleterTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID TO_KEEP_COLUMN_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private TableColumnDeletionService tableColumnDeletionService;

    @InjectMocks
    private EditTableColumnDeleter underTest;

    @Mock
    private TableColumnModel toKeepModel;

    @Mock
    private TableColumnModel newColumnModel;

    @Mock
    private Dimension toKeepColumn;

    @Mock
    private Dimension toDeleteColumn;

    @Test
    void deleteColumns() {
        given(toKeepModel.getItemType()).willReturn(ItemType.EXISTING);
        given(newColumnModel.getItemType()).willReturn(ItemType.NEW);
        given(toKeepModel.getColumnId()).willReturn(TO_KEEP_COLUMN_ID);
        given(dimensionDao.getByExternalReference(ROW_ID)).willReturn(List.of(toKeepColumn, toDeleteColumn));
        given(toKeepColumn.getDimensionId()).willReturn(TO_KEEP_COLUMN_ID);
        given(toDeleteColumn.getDimensionId()).willReturn(UUID.randomUUID());

        underTest.deleteColumns(ROW_ID, List.of(toKeepModel, newColumnModel));

        then(tableColumnDeletionService).should().deleteColumn(toDeleteColumn);
    }
}