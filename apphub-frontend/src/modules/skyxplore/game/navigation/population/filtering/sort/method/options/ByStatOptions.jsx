import React from "react";
import getSelector from "./OptionSelectorFactory";

const ByStatOptions = ({ stats, selectedStat, setSelectedStat }) => {
    return getSelector(
        "skyxplore-game-population-stat-selector",
        selectedStat,
        stats,
        setSelectedStat
    );
}

export default ByStatOptions;