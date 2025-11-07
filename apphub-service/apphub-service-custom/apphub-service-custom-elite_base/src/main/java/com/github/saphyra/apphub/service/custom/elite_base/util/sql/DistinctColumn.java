package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DistinctColumn implements Column{
    private final Column column;

    @Override
    public String get() {
        return "DISTINCT %s".formatted(column.get());
    }
}
