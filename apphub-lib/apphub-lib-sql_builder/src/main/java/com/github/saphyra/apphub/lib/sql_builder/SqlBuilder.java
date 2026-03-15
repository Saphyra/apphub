package com.github.saphyra.apphub.lib.sql_builder;

import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;
import com.github.saphyra.apphub.lib.sql_builder.query.DeleteQuery;
import com.github.saphyra.apphub.lib.sql_builder.query.SelectQuery;

public interface SqlBuilder extends SegmentProvider {
    static SelectQuery select() {
        return new SelectQuery();
    }

    static DeleteQuery delete() {
        return new DeleteQuery();
    }

    String build();
}
