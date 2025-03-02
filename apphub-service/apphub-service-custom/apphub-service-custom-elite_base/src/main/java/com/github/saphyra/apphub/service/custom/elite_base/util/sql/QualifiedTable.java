package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QualifiedTable implements Table {
    private final String schema;
    private final String table;

    @Override
    public String get() {
        return "%s.%s".formatted(schema, table);
    }
}
