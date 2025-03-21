import React from "react";
import EliteBasePage from "../EliteBasePage";
import EliteBaseOverviewPage from "./overview/EliteBaseOverviewPage";
import SearchNearestMaterialTraderPage from "./search_nearest/material_trader/SearchNearestMaterialTraderPage";
import TradeMode from "./commodity_trading/TradeMode";
import TradeCommoditiesPage from "./commodity_trading/trade/TradeCommoditiesPage";
import "./elite_base_pages.css";

const EliteBasePages = ({ openedPage }) => {
    switch (openedPage) {
        case EliteBasePage.INDEX:
            return <EliteBaseOverviewPage />
        case EliteBasePage.SEARCH_NEAREST_MATERIAL_TRADER:
            return <SearchNearestMaterialTraderPage />
        case EliteBasePage.COMMODITY_TRADING_BUY_COMMODITIES:
            return <TradeCommoditiesPage tradeMode={TradeMode.BUY} />
        case EliteBasePage.COMMODITY_TRADING_SELL_COMMODITIES:
            return <TradeCommoditiesPage tradeMode={TradeMode.SELL} />
        default:
            return "Unhandled Page: " + openedPage;
    }
}

export default EliteBasePages;