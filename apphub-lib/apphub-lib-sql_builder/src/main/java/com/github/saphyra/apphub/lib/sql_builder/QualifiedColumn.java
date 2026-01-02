package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QualifiedColumn implements Column {
    private final String table;
    private final String column;

    @Override
    public String get() {
        return String.join(".", table, column);
    }
}
