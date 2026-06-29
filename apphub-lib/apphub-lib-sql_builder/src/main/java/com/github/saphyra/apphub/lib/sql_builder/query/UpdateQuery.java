package com.github.saphyra.apphub.lib.sql_builder.query;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.sql_builder.column.Column;
import com.github.saphyra.apphub.lib.sql_builder.condition.Condition;
import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;
import com.github.saphyra.apphub.lib.sql_builder.table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateQuery {
    private final Table table;
    private final List<BiWrapper<Column, SegmentProvider>> updatedColumns = new ArrayList<>();
    private final List<SegmentProvider> conditions = new ArrayList<>();

    public UpdateQuery(Table table) {
        this.table = table;
    }

    public UpdateQuery set(Column column, SegmentProvider value) {
        updatedColumns.add(new BiWrapper<>(column, value));

        return this;
    }

    public UpdateQuery condition(Condition condition) {
        conditions.add(condition);

        return this;
    }

    public String build() {
        String modificationParts = updatedColumns.stream()
            .map(bw -> "%s=%s".formatted(bw.getEntity1().get(), bw.getEntity2().get()))
            .collect(Collectors.joining(", "));

        List<String> segments = new ArrayList<>();
        segments.add("UPDATE");
        segments.add(table.get());
        segments.add("SET");
        segments.add(modificationParts);

        if (!conditions.isEmpty()) {
            segments.add("WHERE");

            conditions.forEach(condition -> segments.add(condition.get()));
        }

        return String.join(" ", segments);
    }
}
