package com.github.saphyra.apphub.lib.sql_builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Operation {
    GREATER_OR_EQUAL(">="),
    ;
    @Getter
    private final String operation;
}
