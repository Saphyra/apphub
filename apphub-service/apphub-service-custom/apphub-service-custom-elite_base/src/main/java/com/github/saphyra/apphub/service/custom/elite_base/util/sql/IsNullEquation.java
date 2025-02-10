package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IsNullEquation implements Condition {
    private final Column column;

    @Override
    public String get() {
        return "%s is null".formatted(column.get());
    }
}
