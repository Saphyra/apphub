package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSegmentProvider implements SegmentProvider{
    private final Object value;

    @Override
    public String get() {
        return value.toString();
    }
}
