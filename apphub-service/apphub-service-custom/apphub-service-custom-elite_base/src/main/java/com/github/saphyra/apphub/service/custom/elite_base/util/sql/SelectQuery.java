package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class SelectQuery implements SqlBuilder {
    private final List<Column> columns = new ArrayList<>();
    private Table from;
    private final List<Join> joins = new ArrayList<>();
    private Column groupBy;
    private final List<SegmentProvider> conditions = new ArrayList<>();
    private OrderBy orderBy = null;
    private Integer limit = null;
    private Integer offset = 0;

    @Override
    public String build() {
        List<String> segments = new ArrayList<>();

        segments.add("SELECT");

        String columnString = columns.stream()
            .map(SegmentProvider::get)
            .collect(Collectors.joining(", "));
        segments.add(columnString);

        segments.add("FROM");
        segments.add(from.get());

        joins.forEach(join -> segments.add(join.get()));

        if(nonNull(groupBy)){
            segments.add("GROUP BY");
            String groupBySegment = groupBy.get();
            segments.add(groupBySegment);
        }

        if(!conditions.isEmpty()){
            segments.add("WHERE");
        }

        conditions.forEach(segmentProvider -> segments.add(segmentProvider.get()));

        if(nonNull(orderBy)){
            segments.add(orderBy.get());
        }

        if(nonNull(limit)){
            segments.add("LIMIT %s".formatted(limit));

            segments.add("OFFSET %s".formatted(offset));
        }

        return String.join(" ", segments);
    }

    public SelectQuery column(Column column) {
        columns.add(column);
        return this;
    }

    public SelectQuery columns(String... columnNames) {
        Arrays.stream(columnNames)
            .forEach(this::column);
        return this;
    }

    public SelectQuery column(String columnName) {
        columns.add(new DefaultColumn(columnName));
        return this;
    }

    public SelectQuery from(Table table) {
        from = table;

        return this;
    }

    public SelectQuery innerJoin(Table table, Column column1, Column column2) {
        joins.add(new Join(JoinType.INNER, table, column1, column2));

        return this;
    }

    public SelectQuery leftJoin(Table table, Column column1, Column column2) {
        joins.add(new Join(JoinType.LEFT, table, column1, column2));

        return this;
    }

    @Override
    public String get() {
        return "(%s)".formatted(build());
    }

    public SelectQuery condition(Condition condition) {
        conditions.add(condition);

        return this;
    }

    public SelectQuery and() {
        conditions.add(SegmentProvider.AND_SEGMENT);

        return this;
    }

    public SelectQuery orderBy(Column column, OrderType orderBy) {
        this.orderBy = new OrderBy(column, orderBy);

        return this;
    }

    public SelectQuery limit(Integer limit) {
        this.limit = limit;

        return this;
    }

    public SelectQuery offset(int offset) {
        this.offset = offset;

        return this;
    }

    public SqlBuilder groupBy(Column column) {
        this.groupBy = column;

        return this;
    }

    @RequiredArgsConstructor
    enum JoinType{
        LEFT("LEFT JOIN"),
        INNER("INNER JOIN"),
        ;

        private final String value;
    }

    @AllArgsConstructor
    static class Join implements SegmentProvider{
        private final JoinType joinType;
        private final Table table;
        private final Column column1;
        private final Column column2;

        @Override
        public String get() {
            return "%s %s on %s = %s".formatted(joinType.value, table.get(), column1.get(), column2.get());
        }
    }

    @AllArgsConstructor
    static class OrderBy implements SegmentProvider{
        private final Column column;
        private final OrderType orderType;

        @Override
        public String get() {
            return "ORDER BY %S %S".formatted(column.get(), orderType.name());
        }
    }
}
