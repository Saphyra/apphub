package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class WrappedValue implements SegmentProvider {
    private final Object value;

    @Override
    public String get() {
        return "'%s'".formatted(isNull(value) ? null : value.toString());
    }
}
