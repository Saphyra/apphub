package com.github.saphyra.apphub.lib.sql_builder.query;

import com.github.saphyra.apphub.lib.sql_builder.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InsertQuery {
    private final Table table;
    private final List<String> columns = new ArrayList<>();

    public InsertQuery(Table table, Collection<String> columns) {
        this.table = table;
        this.columns.addAll(columns);
    }

    public String build() {
        return "INSERT INTO " +
            table.get() +
            " (" +
            String.join(", ", columns) +
            ") VALUES (" +
            columns.stream().map(column -> ":" + column).collect(Collectors.joining(", ")) +
            ")";
    }
}
