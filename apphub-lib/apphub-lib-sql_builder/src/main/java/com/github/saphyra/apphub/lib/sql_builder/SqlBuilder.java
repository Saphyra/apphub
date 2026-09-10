package com.github.saphyra.apphub.lib.sql_builder;

import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;
import com.github.saphyra.apphub.lib.sql_builder.query.DeleteQuery;
import com.github.saphyra.apphub.lib.sql_builder.query.InsertQuery;
import com.github.saphyra.apphub.lib.sql_builder.query.SelectQuery;
import com.github.saphyra.apphub.lib.sql_builder.query.UpdateQuery;
import com.github.saphyra.apphub.lib.sql_builder.table.Table;

import java.util.Collection;

public interface SqlBuilder extends SegmentProvider {
    static SelectQuery select() {
        return new SelectQuery();
    }

    static DeleteQuery delete() {
        return new DeleteQuery();
    }

    static InsertQuery insert(Table table, Collection<String> columns) {
        return new InsertQuery(table, columns);
    }

    static UpdateQuery update(Table table) {
        return new UpdateQuery(table);
    }

    String build();
}
