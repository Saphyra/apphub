package com.github.saphyra.apphub.api.feature.notebook.model.table;

public enum ColumnType {
    NUMBER,
    TEXT,
    IMAGE,
    FILE,
    CHECKBOX,
    COLOR,
    DATE,
    TIME,
    DATE_TIME,
    MONTH,
    RANGE,
    LINK,
    EMPTY;

    public boolean isFile() {
        return this == FILE || this == IMAGE;
    }
}
