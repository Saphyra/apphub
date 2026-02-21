package com.github.saphyra.apphub.lib.sql_builder.condition;

import com.github.saphyra.apphub.lib.sql_builder.column.Column;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NullCondition implements Condition {
    private final Column column;

    @Override
    public String get() {
        return "%s IS NULL".formatted(column.get());
    }
}
