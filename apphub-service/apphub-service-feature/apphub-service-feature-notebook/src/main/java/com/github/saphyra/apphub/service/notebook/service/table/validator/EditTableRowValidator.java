package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableRowValidator {
    private final EditTableColumnValidator editTableColumnValidator;

    void validateTableRows(List<TableRowModel> rows) {
        ValidationUtil.notNull(rows, "rows");

        rows.forEach(this::validateRow);
    }

    private void validateRow(TableRowModel model) {
        ValidationUtil.notNull(model.getRowIndex(), "row.rowIndex");
        ValidationUtil.notNull(model.getChecked(), "row.checked");

        ValidationUtil.notNull(model.getItemType(), "row.itemType");
        if (model.getItemType() == ItemType.EXISTING) {
            ValidationUtil.notNull(model.getRowId(), "row.rowId");
        }

        editTableColumnValidator.validateColumns(model.getItemType(), model.getColumns());
    }
}
