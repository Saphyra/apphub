package com.github.saphyra.apphub.service.custom.elite_base.util.sql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Operation {
    GREATER_OR_EQUAL(">="),
    ;
    @Getter
    private final String operation;
}
