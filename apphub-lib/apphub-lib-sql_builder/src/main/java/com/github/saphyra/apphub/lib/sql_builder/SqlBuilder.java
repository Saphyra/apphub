package com.github.saphyra.apphub.lib.sql_builder;

public interface SqlBuilder extends SegmentProvider {
    static SelectQuery select() {
        return new SelectQuery();
    }

    static DeleteQuery delete() {
        return new DeleteQuery();
    }

    String build();
}
