package com.github.saphyra.apphub.lib.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FilterOrder {
    ACCESS_TOKEN_FILTER(Integer.MIN_VALUE),
    LOCALE_MANDATORY_FILTER(Integer.MIN_VALUE),
    ROLE_FILTER_ORDER(FilterOrder.ACCESS_TOKEN_FILTER.getFilterOrder() + 1);

    private final int filterOrder;
}
