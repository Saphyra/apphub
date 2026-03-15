package com.github.saphyra.apphub.lib.sql_builder.condition;

import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;
import com.github.saphyra.apphub.lib.sql_builder.column.Column;
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
