package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnCloneService {
    private final DimensionDao dimensionDao;
    private final ColumnDataServiceFetcher columnDataServiceFetcher;
    private final ColumnTypeDao columnTypeDao;

    void cloneColumns(DeprecatedListItem clone, Dimension originalRow, Dimension clonedRow) {
        dimensionDao.getByExternalReference(originalRow.getDimensionId())
            .forEach(column -> cloneColumn(clone, clonedRow.getDimensionId(), column));
    }

    private void cloneColumn(DeprecatedListItem clone, UUID rowId, Dimension column) {
        ColumnType columnType = columnTypeDao.findByIdValidated(column.getDimensionId())
            .getType();

        columnDataServiceFetcher.findColumnDataService(columnType)
            .clone(clone, rowId, column);
    }
}
