package com.github.saphyra.apphub.integration.framework.endpoints;

public class EliteBaseEndpoints {
    public static final String ELITE_BASE_PAGE = "/web/elite-base";

    public static final String ELITE_BASE_STAR_SYSTEMS_SEARCH = "/api/elite-base/star-systems/search";
    public static final String ELITE_BASE_GET_POWERS = "/api/elite-base/powers";
    public static final String ELITE_BASE_GET_POWERPLAY_STATES = "/api/elite-base/powers/powerplay-states";

    //Search nearest
    public static final String ELITE_BASE_NEAREST_MATERIAL_TRADERS = "/api/elite-base/nearest/{starId}/material-traders/{materialType}/{page}";

    //Commodity trading
    public static final String ELITE_BASE_COMMODITY_TRADING_TRADE = "/api/elite-base/commodity-trading/trade";
    public static final String ELITE_BASE_COMMODITY_TRADING_COMMODITIES = "/api/elite-base/commodity-trading/commodities";
    public static final String ELITE_BASE_COMMODITY_TRADING_COMMODITIES_AVERAGE_PRICE = "/api/elite-base/commodity-trading/commodities/{commodityName}/average-price";
}
