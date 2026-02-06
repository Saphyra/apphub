package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PowerSegment implements SegmentProvider {
    private final SegmentProvider value;
    private final double power;

    @Override
    public String get() {
        if (power == 2) {
            return "(%s) * (%s)".formatted(value.get(), value.get());
        }
        return "power(%s, %s)".formatted(value.get(), power);
    }
}
