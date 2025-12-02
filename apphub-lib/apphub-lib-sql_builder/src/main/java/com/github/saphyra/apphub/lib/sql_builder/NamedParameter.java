package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamedParameter implements SegmentProvider {
    private final String name;

    @Override
    public String get() {
        return ":" + name;
    }
}
