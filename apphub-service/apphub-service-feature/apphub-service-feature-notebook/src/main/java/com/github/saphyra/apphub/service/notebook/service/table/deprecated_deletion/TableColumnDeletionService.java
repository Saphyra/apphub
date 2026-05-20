package com.github.saphyra.apphub.service.notebook.service.table.deprecated_deletion;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.DeprecatedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.deprecated_column_data.base.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
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

    private DeprecatedColumnDataService findColumnDataService(UUID columnId) {
        ColumnType columnType = columnTypeDao.findByIdValidated(columnId)
            .getType();

        return columnDataServiceFetcher.findColumnDataService(columnType);
    }
}
