import React from "react";
import EliteBasePage from "../EliteBasePage";
import EliteBaseOverviewPage from "./overview/EliteBaseOverviewPage";
import SearchNearestMaterialTraderPage from "./search_nearest/material_trader/SearchNearestMaterialTraderPage";

const EliteBasePages = ({ openedPage }) => {
    switch (openedPage) {
        case EliteBasePage.INDEX:
            return <EliteBaseOverviewPage />
        case EliteBasePage.SEARCH_NEAREST_MATERIAL_TRADER:
            return <SearchNearestMaterialTraderPage />
        default:
            return "Unhandled Page: " + openedPage;
    }
}

export default EliteBasePages;