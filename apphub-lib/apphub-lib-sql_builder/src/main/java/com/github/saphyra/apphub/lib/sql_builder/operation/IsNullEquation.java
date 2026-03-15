package com.github.saphyra.apphub.lib.sql_builder.operation;

import com.github.saphyra.apphub.lib.sql_builder.column.Column;
import com.github.saphyra.apphub.lib.sql_builder.condition.Condition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IsNullEquation implements Condition {
    private final Column column;

    @Override
    public String get() {
        return "%s is null".formatted(column.get());
    }
}
