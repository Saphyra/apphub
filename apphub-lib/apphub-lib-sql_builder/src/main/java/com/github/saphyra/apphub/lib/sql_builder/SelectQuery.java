package com.github.saphyra.apphub.lib.sql_builder;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class SelectQuery extends AbstractQuery<SelectQuery> {
    private final List<Column> columns = new ArrayList<>();
    private final List<Join> joins = new ArrayList<>();
    private final List<Except> excepts = new ArrayList<>();
    private Column groupBy;

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

        excepts.forEach(except -> segments.add(except.get()));

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

    public SelectQuery innerJoin(Table table, Column column1, Column column2) {
        joins.add(new Join(JoinType.INNER, table, column1, column2));

        return this;
    }

    public SelectQuery leftJoin(Table table, Column column1, Column column2) {
        joins.add(new Join(JoinType.LEFT, table, column1, column2));

        return this;
    }

    public SelectQuery groupBy(Column column) {
        this.groupBy = column;

        return this;
    }

    public SelectQuery except(SelectQuery except){
        this.excepts.add(new Except(except));

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
            return "%s %s ON %s = %s".formatted(joinType.value, table.get(), column1.get(), column2.get());
        }
    }
}
