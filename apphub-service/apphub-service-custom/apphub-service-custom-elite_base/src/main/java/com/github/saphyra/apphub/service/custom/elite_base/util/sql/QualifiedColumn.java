package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

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
