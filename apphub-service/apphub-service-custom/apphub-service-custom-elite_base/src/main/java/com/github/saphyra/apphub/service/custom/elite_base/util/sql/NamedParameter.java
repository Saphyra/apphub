package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamedParameter implements SegmentProvider {
    private final String name;

    @Override
    public String get() {
        return ":" + name;
    }
}
