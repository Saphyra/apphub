package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer_filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OfferFilterOrder {
    DEFAULT_ORDER(0),
    LAST_UPDATED_FILTER_ORDER(100),
    POWER_FILTER_ORDER(200),
    ;

    @Getter
    private final int order;
}
