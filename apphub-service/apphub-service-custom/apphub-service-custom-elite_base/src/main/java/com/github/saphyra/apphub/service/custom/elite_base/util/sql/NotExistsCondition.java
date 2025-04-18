package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotExistsCondition implements Condition {
    private final SqlBuilder query;

    @Override
    public String get() {
        return "NOT EXISTS %s".formatted(query.get());
    }
}
