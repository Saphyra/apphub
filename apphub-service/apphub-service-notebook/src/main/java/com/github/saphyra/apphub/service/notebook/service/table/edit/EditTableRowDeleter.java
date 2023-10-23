package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableRowDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableRowDeleter {
    private final DimensionDao dimensionDao;
    private final TableRowDeletionService tableRowDeletionService;

    void deleteRows(UUID listItemId, List<TableRowModel> rows) {
        List<UUID> toKeep = rows.stream()
                .filter(tableRowModel -> tableRowModel.getItemType() == ItemType.EXISTING)
                .map(TableRowModel::getRowId)
                .toList();

        dimensionDao.getByExternalReference(listItemId)
                .stream()
                .filter(dimension -> !toKeep.contains(dimension.getDimensionId()))
                .forEach(tableRowDeletionService::deleteRow);
    }
}
