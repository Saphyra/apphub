package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TableCreationColumnValidator {
    private final TableColumnDataValidator tableColumnDataValidator;

    void validateColumn(TableColumnModel tableColumnModel) {
        ValidationUtil.notNull(tableColumnModel.getColumnIndex(), "row.column.columnIndex");
        ValidationUtil.notNull(tableColumnModel.getColumnType(), "row.column.columnType");

        tableColumnDataValidator.validate(tableColumnModel.getColumnType(), tableColumnModel.getData());
    }
}
