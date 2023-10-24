package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnCreationService {
    private final ColumnDataServiceFetcher columnDataServiceFetcher;

    List<TableFileUploadResponse> saveColumns(UUID userId, UUID listItemId, Dimension row, List<TableColumnModel> columns) {
        return columns.stream()
            .map(tableColumnModel -> saveColumn(userId, listItemId, row, tableColumnModel))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    private Optional<TableFileUploadResponse> saveColumn(UUID userId, UUID listItemId, Dimension row, TableColumnModel tableColumnModel) {
        return columnDataServiceFetcher.findColumnDataService(tableColumnModel.getColumnType())
            .save(userId, listItemId, row.getDimensionId(), tableColumnModel);
    }
}
