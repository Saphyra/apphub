package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataServiceFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableColumnQueryServiceTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 243;
    private static final Object DATA = "data";

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @InjectMocks
    private TableColumnQueryService underTest;

    @Mock
    private Dimension column;

    @Mock
    private ColumnDataService columnDataService;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Test
    void getColumns() {
        given(dimensionDao.getByExternalReference(ROW_ID)).willReturn(List.of(column));
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(column.getIndex()).willReturn(COLUMN_INDEX);
        given(columnTypeDao.findByIdValidated(COLUMN_ID)).willReturn(columnTypeDto);
        given(columnTypeDto.getType()).willReturn(ColumnType.LINK);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.LINK)).willReturn(columnDataService);
        given(columnDataService.getData(COLUMN_ID)).willReturn(DATA);

        List<TableColumnModel> result = underTest.getColumns(ROW_ID);

        assertThat(result).hasSize(1);
        TableColumnModel model = result.get(0);
        assertThat(model.getColumnId()).isEqualTo(COLUMN_ID);
        assertThat(model.getColumnIndex()).isEqualTo(COLUMN_INDEX);
        assertThat(model.getColumnType()).isEqualTo(ColumnType.LINK);
        assertThat(model.getData()).isEqualTo(DATA);
        assertThat(model.getItemType()).isEqualTo(ItemType.EXISTING);
    }
}