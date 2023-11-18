package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnTypeValidator {
     void validateColumnType(CreateTableRequest request) {
        switch (request.getListItemType()) {
            case TABLE, CHECKLIST_TABLE -> validateColumnsForTable(request.getRows());
            case CUSTOM_TABLE -> validateForCustomTable(request.getRows()); //TODO unit test
            default -> throw ExceptionFactory.invalidParam("listItemType", "not supported");
        }
    }

    private void validateColumnsForTable(List<TableRowModel> rows) {
        rows.stream()
            .flatMap(tableRowModel -> tableRowModel.getColumns().stream())
            .forEach(this::validateTextColumnType);
    }

    //TODO extract
    private void validateForCustomTable(List<TableRowModel> rows) {
        rows.stream()
            .flatMap(tableRowModel -> tableRowModel.getColumns().stream())
            .forEach(this::validateColumnType);
    }

    private void validateColumnType(TableColumnModel columnModel) {
        switch (columnModel.getColumnType()){
            case TEXT -> validateTextColumnType(columnModel);
            case EMPTY -> {}
            default -> throw ExceptionFactory.reportedException("Unhandled columnType " + columnModel.getColumnType());
        }
    }

    private void validateTextColumnType(TableColumnModel tableColumnModel) {
        ValidationUtil.equals(tableColumnModel.getColumnType(), ColumnType.TEXT, "row.column.columnType");
        ValidationUtil.notNull(tableColumnModel.getData(), "row.column.data");
    }
}
