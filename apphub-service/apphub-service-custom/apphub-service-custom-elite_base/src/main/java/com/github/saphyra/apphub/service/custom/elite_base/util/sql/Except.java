package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Except implements SegmentProvider{
    private final SelectQuery query;

    @Override
    public String get() {
        return "EXCEPT %s".formatted(query.build());
    }
}
