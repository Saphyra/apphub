package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
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
class TableColumnCloneServiceTest {
    private static final UUID ORIGINAL_ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID CLONED_ROW_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @InjectMocks
    private TableColumnCloneService underTest;

    @Mock
    private Dimension originalRow;

    @Mock
    private Dimension clonedRow;

    @Mock
    private ColumnDataService columnDataService;

    @Mock
    private ColumnTypeDto columnType;

    @Mock
    private ListItem listItem;

    @Mock
    private Dimension column;

    @Test
    void cloneColumns() {
        given(originalRow.getDimensionId()).willReturn(ORIGINAL_ROW_ID);
        given(dimensionDao.getByExternalReference(ORIGINAL_ROW_ID)).willReturn(List.of(column));
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnType);
        given(columnType.getType()).willReturn(ColumnType.TEXT);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.TEXT)).willReturn(columnDataService);
        given(clonedRow.getDimensionId()).willReturn(CLONED_ROW_ID);

        underTest.cloneColumns(listItem, originalRow, clonedRow);

        then(columnDataService).should().clone(listItem, CLONED_ROW_ID, column);
    }
}