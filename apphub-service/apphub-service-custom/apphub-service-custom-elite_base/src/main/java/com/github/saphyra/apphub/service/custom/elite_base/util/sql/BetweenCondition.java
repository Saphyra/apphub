package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BetweenCondition implements Condition {
    private final SegmentProvider value;
    private final SegmentProvider limit1;
    private final SegmentProvider limit2;

    @Override
    public String get() {
        return String.format("%s BETWEEN %s AND %s", value.get(), limit1.get(), limit2.get());
    }
}
