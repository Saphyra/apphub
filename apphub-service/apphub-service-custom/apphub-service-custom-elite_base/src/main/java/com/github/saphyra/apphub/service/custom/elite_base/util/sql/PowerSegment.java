package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

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
