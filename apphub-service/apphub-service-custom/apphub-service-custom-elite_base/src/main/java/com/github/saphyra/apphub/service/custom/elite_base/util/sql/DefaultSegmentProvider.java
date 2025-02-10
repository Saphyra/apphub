package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSegmentProvider implements SegmentProvider{
    private final Object value;

    @Override
    public String get() {
        return value.toString();
    }
}
