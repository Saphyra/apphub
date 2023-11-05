package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnQueryService {
    private final DimensionDao dimensionDao;
    private final ColumnTypeDao columnTypeDao;
    private final ColumnDataServiceFetcher columnDataServiceFetcher;

    List<TableColumnModel> getColumns(UUID rowId) {
        return dimensionDao.getByExternalReference(rowId)
            .stream()
            .map(column -> {
                ColumnType columnType = columnTypeDao.findByIdValidated(column.getDimensionId()).getType();
                return TableColumnModel.builder()
                    .columnId(column.getDimensionId())
                    .columnIndex(column.getIndex())
                    .columnType(columnType)
                    .data(getDataForColumnType(column.getDimensionId(), columnType))
                    .itemType(ItemType.EXISTING)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private Object getDataForColumnType(UUID columnId, ColumnType columnType) {
        return columnDataServiceFetcher.findColumnDataService(columnType)
            .getData(columnId);
    }
}
