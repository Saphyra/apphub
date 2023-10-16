package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableColumnDataValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
public class EditTableRequestValidator {
    private final ListItemDao listItemDao;
    private final TitleValidator titleValidator;
    private final TableHeadDao tableHeadDao;
    private final DimensionDao dimensionDao;
    private final TableColumnDataValidator tableColumnDataValidator;

    public void validate(UUID listItemId, EditTableRequest request) {
        titleValidator.validate(request.getTitle());

        validateTableHeads(listItemId, request.getTableHeads());
        validateColumnNumbersMatches(request.getTableHeads(), request.getRows());
        validateTableRows(listItemId, request.getRows());
    }

    private void validateTableHeads(UUID listItemId, List<TableHeadModel> tableHeads) {
        ValidationUtil.notNull(tableHeads, "tableHeads");

        tableHeads.forEach(tableHead -> validateTableHead(listItemId, tableHead));
    }

    private void validateTableHead(UUID listItemId, TableHeadModel model) {
        ValidationUtil.notNull(model.getColumnIndex(), "tableHead.columnIndex");
        ValidationUtil.notBlank(model.getContent(), "tableHead.content");
        ValidationUtil.notNull(model.getType(), "tableHead.type");

        if (model.getType() == ItemType.EXISTING) {
            ValidationUtil.notNull(model.getTableHeadId(), "tableHead.tableHeadId");

            TableHead tableHead = tableHeadDao.findByIdValidated(model.getTableHeadId());
            if (!tableHead.getParent().equals(listItemId)) {
                throw ExceptionFactory.invalidParam("tableHead.tableHeadId", "points to different table");
            }
        }
    }

    private void validateTableRows(UUID listItemId, List<TableRowModel> rows) {
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

        validateColumns(allowedColumns, model.getItemType(), model.getColumns());
    }

    private void validateColumns(List<UUID> allowedColumns, ItemType rowItemType, List<TableColumnModel> columns) {
        ValidationUtil.notNull(columns, "row.columns");

        columns.forEach(column -> validateColumn(allowedColumns, rowItemType, column));
    }

    private void validateColumn(List<UUID> allowedColumns, ItemType rowItemType, TableColumnModel model) {
        ValidationUtil.notNull(model.getColumnIndex(), "row.column.columnIndex");

        ValidationUtil.notNull(model.getColumnType(), "row.column.columnType");
        tableColumnDataValidator.validate(model.getColumnType(), model.getData());

        ValidationUtil.notNull(model.getItemType(), "row.column.itemType");
        if (rowItemType == ItemType.NEW && model.getItemType() != ItemType.NEW) {
            throw ExceptionFactory.invalidParam("row.column.itemType", "must be " + ItemType.NEW);
        }
        if (model.getItemType() == ItemType.EXISTING && !allowedColumns.contains(model.getColumnId())) {
            dimensionDao.findByIdValidated(model.getColumnId()); //To check existence
            throw ExceptionFactory.invalidParam("row.column.columnId", "points to different table");
        }
    }

    private void validateColumnNumbersMatches(List<TableHeadModel> tableHeads, List<TableRowModel> rows) {
        boolean hasMismatch = rows.stream()
            .anyMatch(tableRowModel -> tableRowModel.getColumns().size() != tableHeads.size());
        if (hasMismatch) {
            throw ExceptionFactory.invalidParam("row.columns", "item count mismatch");
        }
    }
}
