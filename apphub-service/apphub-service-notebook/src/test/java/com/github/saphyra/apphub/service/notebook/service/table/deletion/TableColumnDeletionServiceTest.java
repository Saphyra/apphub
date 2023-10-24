package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
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
class TableColumnDeletionServiceTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @Mock
    private DimensionDao dimensionDao;

    @InjectMocks
    private TableColumnDeletionService underTest;

    @Mock
    private Dimension column;

    @Mock
    private ColumnDataService columnDataService;

    @Mock
    private ColumnTypeDto columnType;

    @Test
    void deleteColumnsOfRow() {
        given(dimensionDao.getByExternalReference(ROW_ID)).willReturn(List.of(column));
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnType);
        given(columnType.getType()).willReturn(ColumnType.TEXT);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.TEXT)).willReturn(columnDataService);

        underTest.deleteColumnsOfRow(ROW_ID);

        then(columnDataService).should().delete(column);
    }
}