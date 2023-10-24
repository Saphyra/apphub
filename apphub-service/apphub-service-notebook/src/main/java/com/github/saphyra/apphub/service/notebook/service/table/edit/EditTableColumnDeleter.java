package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableColumnDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
class EditTableColumnDeleter {
    private final DimensionDao dimensionDao;
    private final TableColumnDeletionService tableColumnDeletionService;

    void deleteColumns(UUID rowId, List<TableColumnModel> columns) {
        List<UUID> toKeep = columns.stream()
            .filter(model -> model.getItemType() == ItemType.EXISTING)
            .map(TableColumnModel::getColumnId)
            .toList();

        dimensionDao.getByExternalReference(rowId)
            .stream()
            .filter(dimension -> !toKeep.contains(dimension.getDimensionId()))
            .forEach(tableColumnDeletionService::deleteColumn);
    }
}
