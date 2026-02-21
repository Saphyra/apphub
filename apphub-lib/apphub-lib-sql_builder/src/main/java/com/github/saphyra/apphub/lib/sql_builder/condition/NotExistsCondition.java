package com.github.saphyra.apphub.lib.sql_builder.condition;

import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotExistsCondition implements Condition {
    private final SqlBuilder query;

    @Override
    public String get() {
        return "NOT EXISTS %s".formatted(query.get());
    }
}
