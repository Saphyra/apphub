package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableRowDeletionService;
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
class EditTableRowDeleterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TO_KEEP_ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private TableRowDeletionService tableRowDeletionService;

    @InjectMocks
    private EditTableRowDeleter underTest;

    @Mock
    private TableRowModel existingModel;

    @Mock
    private TableRowModel newModel;

    @Mock
    private Dimension toKeepRow;

    @Mock
    private Dimension toDeleteRow;

    @Test
    void deleteRows() {
        given(existingModel.getItemType()).willReturn(ItemType.EXISTING);
        given(newModel.getItemType()).willReturn(ItemType.NEW);
        given(existingModel.getRowId()).willReturn(TO_KEEP_ROW_ID);
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(toKeepRow, toDeleteRow));
        given(toKeepRow.getDimensionId()).willReturn(TO_KEEP_ROW_ID);
        given(toDeleteRow.getDimensionId()).willReturn(UUID.randomUUID());

        underTest.deleteRows(LIST_ITEM_ID, List.of(existingModel, newModel));

        then(tableRowDeletionService).should().deleteRow(toDeleteRow);
    }
}