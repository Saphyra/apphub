package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OperationCondition implements Condition {
    private final SegmentProvider value1;
    private final Operation operation;
    private final SegmentProvider value2;

    @Override
    public String get() {
        return String.format("%s %s %s", value1.get(), operation.getOperation(), value2.get());
    }
}
