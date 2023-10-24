package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class TableAndChecklistTableColumnTypeValidator {
     void validateColumnType(CreateTableRequest request) {
        switch (request.getListItemType()) {
            case TABLE, CHECKLIST_TABLE -> validateColumnsForTable(request.getRows());
            case CUSTOM_TABLE -> throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled listItemType: " + request.getListItemType());
            default -> throw ExceptionFactory.invalidParam("listItemType", "not supported");
        }
    }

    private void validateColumnsForTable(List<TableRowModel> rows) {
        rows.stream()
            .flatMap(tableRowModel -> tableRowModel.getColumns().stream())
            .forEach(this::validateTextColumnType);
    }

    private void validateTextColumnType(TableColumnModel tableColumnModel) {
        ValidationUtil.equals(tableColumnModel.getColumnType(), ColumnType.TEXT, "row.column.columnType");
        ValidationUtil.notNull(tableColumnModel.getData(), "row.column.data");
    }
}
