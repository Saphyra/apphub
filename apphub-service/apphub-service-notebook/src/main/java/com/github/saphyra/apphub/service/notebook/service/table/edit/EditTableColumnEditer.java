package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableColumnDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableColumnEditer {
    private final TableColumnDeletionService tableColumnDeletionService;
    private final ColumnTypeDao columnTypeDao;
    private final ColumnDataServiceFetcher columnDataServiceFetcher;

    List<TableFileUploadResponse> editTableColumn(ListItem listItem, UUID rowId, TableColumnModel columnModel) {
        if (columnModel.getItemType() == ItemType.EXISTING) {
            ColumnType originalColumnType = getColumnType(columnModel.getColumnId());
            if (columnModel.getColumnType() == originalColumnType) {
                return columnDataServiceFetcher.findColumnDataService(originalColumnType)
                    .edit(listItem, rowId, columnModel)
                    .stream()
                    .toList();
            } else {
                tableColumnDeletionService.deleteColumn(columnModel.getColumnId());
                //Jump out of if, and continue with new creation
            }
        }

        return columnDataServiceFetcher.findColumnDataService(columnModel.getColumnType())
            .save(listItem.getUserId(), listItem.getListItemId(), rowId, columnModel)
            .stream()
            .toList();
    }

    private ColumnType getColumnType(UUID columnId) {
        return columnTypeDao.findByIdValidated(columnId)
            .getType();
    }
}
