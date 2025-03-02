package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Equation implements Condition {
    private final SegmentProvider value1;
    private final SegmentProvider value2;

    @Override
    public String get() {
        return "%s=%s".formatted(value1.get(), value2.get());
    }
}
