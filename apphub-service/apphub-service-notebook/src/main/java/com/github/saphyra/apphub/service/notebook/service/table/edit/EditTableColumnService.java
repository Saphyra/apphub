package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableColumnDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableColumnService {
    private final DimensionDao dimensionDao;
    private final TableColumnDeletionService tableColumnDeletionService;
    private final ColumnTypeDao columnTypeDao;
    private final List<ColumnDataService> columnDataServices;

    List<TableFileUploadResponse> editTableColumns(ListItem listItem, UUID rowId, List<TableColumnModel> columns) {
        deleteColumns(rowId, columns);

        return columns.stream()
            .flatMap(column -> editTableColumn(listItem, rowId, column).stream())
            .collect(Collectors.toList());
    }

    private void deleteColumns(UUID rowId, List<TableColumnModel> columns) {
        List<UUID> toKeep = columns.stream()
            .filter(model -> model.getItemType() == ItemType.EXISTING)
            .map(TableColumnModel::getColumnId)
            .toList();

        dimensionDao.getByExternalReference(rowId)
            .stream()
            .filter(dimension -> !toKeep.contains(dimension.getDimensionId()))
            .forEach(tableColumnDeletionService::deleteColumn);
    }

    private List<TableFileUploadResponse> editTableColumn(ListItem listItem, UUID rowId, TableColumnModel columnModel) {
        if (columnModel.getItemType() == ItemType.EXISTING) {
            ColumnType originalColumnType = getColumnType(columnModel.getColumnId());
            if (columnModel.getColumnType() == originalColumnType) {
                return findColumnDataService(originalColumnType)
                    .edit(listItem, rowId, columnModel)
                    .stream()
                    .toList();
            } else {
                tableColumnDeletionService.deleteColumn(dimensionDao.findByIdValidated(columnModel.getColumnId()));
                //Jump out of if, and continue with new creation
            }
        }

        return findColumnDataService(columnModel.getColumnType())
            .save(listItem.getUserId(), listItem.getListItemId(), rowId, columnModel)
            .stream()
            .toList();
    }

    private ColumnType getColumnType(UUID columnId) {
        return columnTypeDao.findByIdValidated(columnId)
            .getType();
    }

    private ColumnDataService findColumnDataService(ColumnType columnType) {
        return columnDataServices.stream()
            .filter(service -> service.canProcess(columnType))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.reportedException("No ColumnDataService found for columnType " + columnType));
    }
}
