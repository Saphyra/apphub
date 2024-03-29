package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableColumnDeletionService {
    private final ColumnTypeDao columnTypeDao;
    private final ColumnDataServiceFetcher columnDataServiceFetcher;
    private final DimensionDao dimensionDao;

    public void deleteColumnsOfRow(UUID rowId) {
        dimensionDao.getByExternalReference(rowId)
            .forEach(this::deleteColumn);
    }

    public void deleteColumn(UUID columnId) {
        deleteColumn(dimensionDao.findByIdValidated(columnId));
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
