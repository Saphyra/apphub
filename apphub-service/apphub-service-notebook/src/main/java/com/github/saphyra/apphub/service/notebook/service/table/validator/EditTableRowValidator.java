package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableRowValidator {
    private final ListItemDao listItemDao;
    private final DimensionDao dimensionDao;
    private final EditTableColumnValidator editTableColumnValidator;

    void validateTableRows(UUID listItemId, List<TableRowModel> rows) {
        ValidationUtil.notNull(rows, "rows");

        rows.forEach(row -> validateRow(listItemId, row));
    }

    private void validateRow(UUID listItemId, TableRowModel model) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        ValidationUtil.notNull(model.getRowIndex(), "row.rowIndex");
        if (listItem.getType() == ListItemType.CHECKLIST_TABLE) {
            ValidationUtil.notNull(model.getChecked(), "row.checked");
        }

        ValidationUtil.notNull(model.getItemType(), "row.itemType");
        if (model.getItemType() == ItemType.EXISTING) {
            Dimension row = dimensionDao.findByIdValidated(model.getRowId());
            if (!row.getExternalReference().equals(listItemId)) {
                throw ExceptionFactory.invalidParam("row.rowId", "points to different table");
            }
        }

        List<UUID> allowedColumns = dimensionDao.getByExternalReference(model.getRowId())
            .stream()
            .map(Dimension::getDimensionId)
            .toList();

        editTableColumnValidator.validateColumns(allowedColumns, model.getItemType(), model.getColumns());
    }
}
