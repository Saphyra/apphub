package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableColumnValidator {
    private final DimensionDao dimensionDao;
    private final TableColumnDataValidator tableColumnDataValidator;

    void validateColumns(UUID rowId, ItemType rowItemType, List<TableColumnModel> columns) {
        ValidationUtil.notNull(columns, "row.columns");

        List<UUID> allowedColumns = Optional.ofNullable(rowId)
            .map(dimensionDao::getByExternalReference)
            .orElse(Collections.emptyList())
            .stream()
            .map(Dimension::getDimensionId)
            .toList();

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
}
