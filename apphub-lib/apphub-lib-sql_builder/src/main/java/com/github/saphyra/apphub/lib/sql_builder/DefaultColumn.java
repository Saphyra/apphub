package com.github.saphyra.apphub.lib.sql_builder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultColumn implements Column {
    private final String column;

    @Override
    public String get() {
        return column;
    }
}
