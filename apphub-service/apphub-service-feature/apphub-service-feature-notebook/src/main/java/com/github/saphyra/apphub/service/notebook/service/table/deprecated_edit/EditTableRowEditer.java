package com.github.saphyra.apphub.service.notebook.service.table.deprecated_edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class EditTableRowEditer {
    private final DimensionDao dimensionDao;
    private final DimensionFactory dimensionFactory;
    private final CheckedItemDao checkedItemDao;
    private final CheckedItemFactory checkedItemFactory;
    private final EditTableColumnService editTableColumnService;

    List<TableFileUploadResponse> updateRows(DeprecatedListItem listItem, List<TableRowModel> rows) {
        return rows.stream()
            .flatMap(tableRowModel -> updateRow(listItem, tableRowModel).stream())
            .collect(Collectors.toList());
    }

    private List<TableFileUploadResponse> updateRow(DeprecatedListItem listItem, TableRowModel rowModel) {
        Dimension row = getRow(listItem, rowModel);
        dimensionDao.save(row);

        if (listItem.getType() == ListItemType.CHECKLIST_TABLE) {
            CheckedItem checkedItem = getCheckedItem(listItem, rowModel.getItemType(), row.getDimensionId(), rowModel.getChecked());
            checkedItemDao.save(checkedItem);
        }

        return editTableColumnService.editTableColumns(listItem, row.getDimensionId(), rowModel.getColumns());
    }

    private Dimension getRow(DeprecatedListItem listItem, TableRowModel rowModel) {
        if (rowModel.getItemType() == ItemType.EXISTING) {
            Dimension row = dimensionDao.findByIdValidated(rowModel.getRowId());
            row.setIndex(rowModel.getRowIndex());
            return row;
        } else {
            return dimensionFactory.create(listItem.getUserId(), listItem.getListItemId(), rowModel.getRowIndex());
        }
    }

    private CheckedItem getCheckedItem(DeprecatedListItem listItem, ItemType itemType, UUID rowId, Boolean checked) {
        if (itemType == ItemType.EXISTING) {
            CheckedItem checkedItem = checkedItemDao.findByIdValidated(rowId);
            checkedItem.setChecked(checked);
            return checkedItem;
        } else {
            return checkedItemFactory.create(listItem.getUserId(), rowId, checked);
        }
    }
}
