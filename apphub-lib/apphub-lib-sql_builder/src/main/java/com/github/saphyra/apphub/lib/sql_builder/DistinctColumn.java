package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DistinctColumn implements Column{
    private final Column column;

    @Override
    public String get() {
        return "DISTINCT %s".formatted(column.get());
    }
}
