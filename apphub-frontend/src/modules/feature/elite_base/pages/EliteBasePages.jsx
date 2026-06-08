import React from "react";
import EliteBasePage from "../EliteBasePage";
import EliteBaseOverviewPage from "./overview/EliteBaseOverviewPage";
import SearchNearestMaterialTraderPage from "./search_nearest/material_trader/SearchNearestMaterialTraderPage";
import TradeMode from "./commodity_trading/TradeMode";
import TradeCommoditiesPage from "./commodity_trading/trade/TradeCommoditiesPage";
import "./elite_base_pages.css";
import MeritFarmMiningPage from "./merit_farm/mining/MeritFarmMiningPage";

const EliteBasePages = ({ openedPage, setDisplaySpinner }) => {
    switch (openedPage) {
        case EliteBasePage.INDEX:
            return <EliteBaseOverviewPage />
        case EliteBasePage.SEARCH_NEAREST_MATERIAL_TRADER:
            return <SearchNearestMaterialTraderPage />
        case EliteBasePage.COMMODITY_TRADING_BUY_COMMODITIES:
            return <TradeCommoditiesPage tradeMode={TradeMode.BUY} />
        case EliteBasePage.COMMODITY_TRADING_SELL_COMMODITIES:
            return <TradeCommoditiesPage tradeMode={TradeMode.SELL} />
        case EliteBasePage.MERIT_FARMING_NAKATO_KAINE_MINING:
            return <MeritFarmMiningPage setDisplaySpinner={setDisplaySpinner} />
        default:
            return "Unhandled Page: " + openedPage;
    }
}

export default EliteBasePages;