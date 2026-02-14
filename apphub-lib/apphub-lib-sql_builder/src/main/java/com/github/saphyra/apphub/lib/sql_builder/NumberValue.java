package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NumberValue implements SegmentProvider{
    private final Number number;

    @Override
    public String get() {
        return number.toString();
    }
}
