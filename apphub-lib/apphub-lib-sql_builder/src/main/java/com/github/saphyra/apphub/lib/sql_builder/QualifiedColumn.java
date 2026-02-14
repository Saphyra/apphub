package com.github.saphyra.apphub.lib.sql_builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QualifiedColumn implements Column {
    @Getter
    private final String table;
    @Getter
    private final String column;

    @Override
    public String get() {
        return String.join(".", table, column);
    }
}
