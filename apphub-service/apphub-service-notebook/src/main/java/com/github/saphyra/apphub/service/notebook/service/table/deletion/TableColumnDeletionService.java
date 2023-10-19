package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableColumnDeletionService {
    private final ColumnTypeDao columnTypeDao;
    private final ColumnDataServiceFetcher columnDataServiceFetcher;
    private final DimensionDao dimensionDao;

    public void deleteColumnsOfRow(UUID rowId) {
        dimensionDao.getByExternalReference(rowId)
            .forEach(column -> findColumnDataService(column.getDimensionId()).delete(column));
    }

    public void deleteColumn(Dimension column) {
        findColumnDataService(column.getDimensionId())
            .delete(column);
    }

    private ColumnDataService findColumnDataService(UUID columnId) {
        ColumnType columnType = columnTypeDao.findByIdValidated(columnId)
            .getType();

        return columnDataServiceFetcher.findColumnDataService(columnType);
    }
}
