package com.github.saphyra.apphub.lib.sql_builder;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractQuery<T extends AbstractQuery<T>> implements SqlBuilder {
    protected Table from;
    protected final List<SegmentProvider> conditions = new ArrayList<>();
    protected OrderBy orderBy = null;
    protected Integer limit = null;
    protected Integer offset = 0;

    @Override
    public String get() {
        return "(%s)".formatted(build());
    }

    public T from(Table table) {
        from = table;

        return self();
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    public T condition(Condition condition) {
        conditions.add(condition);

        return self();
    }

    public T and() {
        conditions.add(SegmentProvider.AND_SEGMENT);

        return self();
    }

    public T or() {
        conditions.add(SegmentProvider.OR_SEGMENT);

        return self();
    }

    public T orderBy(Column column, OrderType orderBy) {
        this.orderBy = new OrderBy(column, orderBy);

        return self();
    }

    public T limit(Integer limit) {
        this.limit = limit;

        return self();
    }

    public T offset(int offset) {
        this.offset = offset;

        return self();
    }

    @AllArgsConstructor
    static class OrderBy implements SegmentProvider {
        private final Column column;
        private final OrderType orderType;

        @Override
        public String get() {
            return "ORDER BY %s %s".formatted(column.get(), orderType.name());
        }
    }
}
