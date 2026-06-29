package com.github.saphyra.apphub.lib.sql_builder.query;

import com.github.saphyra.apphub.lib.sql_builder.table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InsertQuery {
    private final Table table;
    private final Map<String, String> data = new HashMap<>();

    public InsertQuery(Table table, Map<String, String> data) {
        this.table = table;
        this.data.putAll(data);
    }

    public String build() {
        List<String> columns = new ArrayList<>(data.keySet());
        List<String > values = new ArrayList<>(data.values());

        return "INSERT INTO " +
            table.get() +
            " (" +
            String.join(", ", columns) +
            ") VALUES (" +
            values.stream().map("'%s'"::formatted).collect(Collectors.joining(", ")) +
            ")";
    }
}
