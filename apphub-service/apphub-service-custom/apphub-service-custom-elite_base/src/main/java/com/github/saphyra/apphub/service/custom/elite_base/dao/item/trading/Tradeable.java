package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.TradeMode;

import java.util.UUID;

public interface Tradeable {
    UUID getExternalReference();

    String getItemName();

    ItemType getItemType();

    default Integer getTradeAmount(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> getStock();
            case SELL -> getDemand();
        };
    }

    Integer getDemand();

    Integer getStock();


    default Integer getPrice(TradeMode tradeMode) {
        return switch (tradeMode) {
            case BUY -> getSellPrice();
            case SELL -> getBuyPrice();
        };
    }

    Integer getBuyPrice();

    Integer getSellPrice();
}
