package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class TableCreationRowValidator {
    private final TableCreationColumnValidator tableCreationColumnValidator;

    void validateRows(ListItemType listItemType, List<TableRowModel> rows) {
        ValidationUtil.notNull(rows, "rows");
        rows.forEach(rowModel -> validateRow(listItemType, rowModel));
    }

    private void validateRow(ListItemType listItemType, TableRowModel tableRowModel) {
        ValidationUtil.notNull(tableRowModel.getRowIndex(), "row.rowIndex");
        ValidationUtil.notNull(tableRowModel.getColumns(), "row.columns");

        if (listItemType == ListItemType.CHECKLIST_TABLE) {
            ValidationUtil.notNull(tableRowModel.getChecked(), "row.checked");
        }

        tableRowModel.getColumns()
            .forEach(tableCreationColumnValidator::validateColumn);
    }
}
