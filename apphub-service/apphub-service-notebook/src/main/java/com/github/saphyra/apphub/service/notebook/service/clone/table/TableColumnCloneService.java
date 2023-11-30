package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
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

    void cloneColumns(ListItem clone, Dimension originalRow, Dimension clonedRow) {
        dimensionDao.getByExternalReference(originalRow.getDimensionId())
            .forEach(column -> cloneColumn(clone, clonedRow.getDimensionId(), column));
    }

    private void cloneColumn(ListItem clone, UUID rowId, Dimension column) {
        ColumnType columnType = columnTypeDao.findByIdValidated(column.getDimensionId())
            .getType();

        columnDataServiceFetcher.findColumnDataService(columnType)
            .clone(clone, rowId, column);
    }
}
