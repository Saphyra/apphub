package com.github.saphyra.apphub.service.platform.main_gateway.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FilterOrder {
    AUTHENTICATION_FILTER(0),
    PAGE_VISITED_FILTER(AUTHENTICATION_FILTER.getOrder() + 1),
    LOCALE_FILTER(Integer.MIN_VALUE + 1),
    REQUEST_LOGGING_FILTER(Integer.MIN_VALUE),
    LOCALE_COOKIE_FILTER(0);

    @Getter
    private final int order;

}
