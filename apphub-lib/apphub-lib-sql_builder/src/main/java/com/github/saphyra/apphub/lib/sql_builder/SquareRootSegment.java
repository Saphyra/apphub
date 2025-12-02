package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SquareRootSegment implements SegmentProvider{
    private final SegmentProvider value;

    @Override
    public String get() {
        return "sqrt(%s)".formatted(value.get());
    }
}
