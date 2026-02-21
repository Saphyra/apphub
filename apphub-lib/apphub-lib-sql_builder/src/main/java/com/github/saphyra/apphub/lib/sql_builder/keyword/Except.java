package com.github.saphyra.apphub.lib.sql_builder.keyword;

import com.github.saphyra.apphub.lib.sql_builder.core.SegmentProvider;
import com.github.saphyra.apphub.lib.sql_builder.query.SelectQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Except implements SegmentProvider {
    private final SelectQuery query;

    @Override
    public String get() {
        return "EXCEPT %s".formatted(query.build());
    }
}
