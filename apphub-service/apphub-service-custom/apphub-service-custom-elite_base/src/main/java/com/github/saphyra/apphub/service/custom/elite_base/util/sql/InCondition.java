package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InCondition implements Condition {
    private final Column column;
    private final SegmentProvider values;

    @Override
    public String get() {
        return "%s in (%s)".formatted(column.get(), values.get());
    }
}
