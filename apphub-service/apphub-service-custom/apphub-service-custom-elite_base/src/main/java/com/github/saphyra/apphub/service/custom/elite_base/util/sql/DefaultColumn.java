package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultColumn implements Column {
    private final String column;

    @Override
    public String get() {
        return column;
    }
}
