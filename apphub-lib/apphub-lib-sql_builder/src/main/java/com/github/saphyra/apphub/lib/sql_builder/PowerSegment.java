package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PowerSegment implements SegmentProvider {
    private final SegmentProvider value;
    private final double power;

    @Override
    public String get() {
        return "power(%s, %s)".formatted(value.get(), power);
    }
}
