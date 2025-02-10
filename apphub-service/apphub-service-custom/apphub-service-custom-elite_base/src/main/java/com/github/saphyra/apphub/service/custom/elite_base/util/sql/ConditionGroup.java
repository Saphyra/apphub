package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionGroup implements Condition {
    private final List<SegmentProvider> conditions = new ArrayList<>();

    public ConditionGroup condition(Condition condition) {
        conditions.add(condition);

        return this;
    }

    public ConditionGroup and() {
        conditions.add(SegmentProvider.AND_SEGMENT);

        return this;
    }

    public ConditionGroup or() {
        conditions.add(SegmentProvider.OR_SEGMENT);

        return this;
    }

    @Override
    public String get() {
        return "(%s)".formatted(conditions.stream().map(SegmentProvider::get).collect(Collectors.joining(" ")));
    }
}
