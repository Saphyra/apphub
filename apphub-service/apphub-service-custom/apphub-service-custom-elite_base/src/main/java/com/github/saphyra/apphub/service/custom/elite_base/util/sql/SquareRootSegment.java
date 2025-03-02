package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SquareRootSegment implements SegmentProvider{
    private final SegmentProvider value;

    @Override
    public String get() {
        return "sqrt(%s)".formatted(value.get());
    }
}
