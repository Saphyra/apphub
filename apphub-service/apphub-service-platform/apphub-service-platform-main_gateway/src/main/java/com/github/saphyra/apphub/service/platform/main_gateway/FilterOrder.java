package com.github.saphyra.apphub.service.platform.main_gateway;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FilterOrder {
    AUTHENTICATION_FILTER(0),
    LOGGING_FILTER(Integer.MIN_VALUE);

    @Getter
    private final int order;

}
