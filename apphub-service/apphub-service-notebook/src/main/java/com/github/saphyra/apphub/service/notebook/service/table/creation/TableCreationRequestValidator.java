package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.table.*;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
class TableCreationRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    void validate(CreateTableRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        validateTableHeads(request.getTableHeads());
        validateRows(request.getRows());
        validateColumnNumbersMatches(request.getTableHeads(), request.getRows());

        switch (ValidationUtil.convertToEnumChecked(request.getListItemType(), Function.identity(), "listItemType")) {
            case TABLE -> {
                validateColumnsForTable(request.getRows());
            }
            case CHECKLIST_TABLE, CUSTOM_TABLE ->
                    throw ExceptionFactory.notLoggedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled listItemType: " + request.getListItemType());
            default -> throw ExceptionFactory.invalidParam("listItemType", "not supported");
        }
    }

    private void validateColumnsForTable(List<TableRowModel> rows) {
        rows.stream()
                .flatMap(tableRowModel -> tableRowModel.getColumns().stream())
                .forEach(this::validateTextColumnType);
    }

    private void validateTextColumnType(TableColumnModel tableColumnModel) {
        ValidationUtil.equals(tableColumnModel.getColumnType(), ColumnType.TEXT, "row.column.data");
        ValidationUtil.notNull(tableColumnModel.getData(), "row.column.data");
    }

    private void validateRows(List<TableRowModel> rows) {
        ValidationUtil.notNull(rows, "rows");
        rows.forEach(this::validateRow);
    }

    private void validateRow(TableRowModel tableRowModel) {
        ValidationUtil.notNull(tableRowModel.getRowIndex(), "row.rowIndex");
        ValidationUtil.notNull(tableRowModel.getColumns(), "row.columns");

        tableRowModel.getColumns()
                .forEach(this::validateColumn);
    }

    private void validateColumn(TableColumnModel tableColumnModel) {
        ValidationUtil.notNull(tableColumnModel.getColumnIndex(), "row.column.columnIndex");
        ValidationUtil.notNull(tableColumnModel.getColumnType(), "row.column.columnType");
    }

    private void validateTableHeads(List<TableHeadModel> tableHeads) {
        ValidationUtil.notNull(tableHeads, "tableHeads");

        tableHeads.forEach(this::validateTableHead);
    }

    private void validateTableHead(TableHeadModel tableHeadModel) {
        ValidationUtil.notNull(tableHeadModel.getColumnIndex(), "tableHead.columnIndex");
        ValidationUtil.notBlank(tableHeadModel.getContent(), "tableHead.content");
    }

    private void validateColumnNumbersMatches(List<TableHeadModel> tableHeads, List<TableRowModel> rows) {
        boolean hasMismatch = rows.stream()
                .anyMatch(tableRowModel -> tableRowModel.getColumns().size() != tableHeads.size());
        if (hasMismatch) {
            throw ExceptionFactory.invalidParam("rows.columns", "item count mismatch");
        }
    }
}
