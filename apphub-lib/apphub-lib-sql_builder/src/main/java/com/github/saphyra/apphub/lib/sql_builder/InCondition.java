package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InCondition implements Condition {
    private final Column column;
    private final SegmentProvider values;

    @Override
    public String get() {
        return "%s in %s".formatted(column.get(), values.get());
    }
}
