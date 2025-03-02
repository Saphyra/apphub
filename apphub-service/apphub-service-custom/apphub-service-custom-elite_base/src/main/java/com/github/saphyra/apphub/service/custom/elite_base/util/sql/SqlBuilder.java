package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

public interface SqlBuilder extends SegmentProvider{
    static SelectQuery select() {
        return new SelectQuery();
    }

    String build();
}
