package com.github.saphyra.apphub.integration.structure.view.elite_base;

import lombok.Getter;

@Getter
public enum EliteBaseSubPage {
    MATERIAL_TRADER("elite-base-navigation-button-search_nearest_material_trader", EliteBasePageCategory.SEARCH_NEAREST),
    BUY_COMMODITIES("elite-base-navigation-item-commodity_trading_buy_commodities", EliteBasePageCategory.TRADE),
    SELL_COMMODITIES("elite-base-navigation-item-commodity_trading_sell_commodities", EliteBasePageCategory.TRADE),
    MERIT_MINER("elite-base-navigation-item-merit_farming_nakato_kaine_mining", EliteBasePageCategory.FARM_MERITS, EliteBasePageCategory.FOR_NAKATO_KAINE),
    ;

    private final String id;
    private final EliteBasePageCategory[] parents;

    EliteBaseSubPage(String id, EliteBasePageCategory... parents) {
        this.id = id;
        this.parents = parents;
    }
}
