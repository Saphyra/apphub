package com.github.saphyra.apphub.integration.structure.api.notebook;

import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.CheckedTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.NumberTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum ColumnType {
    NUMBER("number", tableColumn -> new NumberTableColumn(tableColumn.getWebElement())),
    TEXT("text", Function.identity()),
    IMAGE("image", null),
    FILE("file", null),
    CHECKBOX("checkbox", tableColumn -> new CheckedTableColumn(tableColumn.getWebElement())),
    COLOR("color", null),
    DATE("date", null),
    TIME("time", null),
    DATE_TIME("date_time", null),
    MONTH("month", null),
    RANGE("range", null),
    LINK("link", Function.identity()),
    EMPTY("empty", null);

    private static final String COLUMN_TYPE_SELECTOR_MASK = "notebook-table-column-type-selector-column-type-%s";

    private final String columnTypeSelectorClass;
    private final Function<TableColumn, ?> converter;

    ColumnType(String columnTypeSelectorClass, Function<TableColumn, ?> converter) {
        this.columnTypeSelectorClass = COLUMN_TYPE_SELECTOR_MASK.formatted(columnTypeSelectorClass);
        this.converter = converter;
    }
}
