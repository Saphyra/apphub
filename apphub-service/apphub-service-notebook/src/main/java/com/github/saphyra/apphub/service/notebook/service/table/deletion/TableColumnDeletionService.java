package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableColumnDeletionService {
    private final ColumnTypeDao columnTypeDao;
    private final List<ColumnDataService> columnDataServices;
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

        return findColumnDataService(columnType);
    }

    private ColumnDataService findColumnDataService(ColumnType columnType) {
        return columnDataServices.stream()
            .filter(columnDataService -> columnDataService.canProcess(columnType))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException("No ColumnDataService found for columnType " + columnType));
    }
}
