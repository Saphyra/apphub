package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Except implements SegmentProvider{
    private final SelectQuery query;

    @Override
    public String get() {
        return "EXCEPT %s".formatted(query.build());
    }
}
