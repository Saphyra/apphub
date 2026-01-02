package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotExistsCondition implements Condition {
    private final SqlBuilder query;

    @Override
    public String get() {
        return "NOT EXISTS %s".formatted(query.get());
    }
}
