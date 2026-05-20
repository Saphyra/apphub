package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableColumnValidator {
    private final TableColumnDataValidator tableColumnDataValidator;

    void validateColumns(ItemType rowItemType, List<TableColumnModel> columns) {
        ValidationUtil.notNull(columns, "row.columns");

        columns.forEach(column -> validateColumn(rowItemType, column));
    }

    private void validateColumn(ItemType rowItemType, TableColumnModel model) {
        ValidationUtil.notNull(model.getColumnIndex(), "row.column.columnIndex");

        ValidationUtil.notNull(model.getColumnType(), "row.column.columnType");
        tableColumnDataValidator.validate(model.getColumnType(), model.getData());

        ValidationUtil.notNull(model.getItemType(), "row.column.itemType");
        if (rowItemType == ItemType.NEW && model.getItemType() != ItemType.NEW) {
            throw ExceptionFactory.invalidParam("row.column.itemType", "must be " + ItemType.NEW);
        }
    }
}
