package com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading;

import lombok.RequiredArgsConstructor;

/**
 * Indicates what the player intends to do
 */
@RequiredArgsConstructor
public enum TradeMode {
    /**
     * Player wants to buy commodities. Matches with station sellPrice and supply.
     */
    BUY,
    /**
     * Player wants to sell commodities. Matches with station buyPrice and demand.
     */
    SELL
}
