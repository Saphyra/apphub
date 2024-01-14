import React from "react";
import buildingLocalizationData from "../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";

const ConstructionHeader = ({ queueItem, localizationHandler }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    return (
        <div className="skyxplore-game-planet-queue-item-header">
            <span className="skyxplore-game-planet-queue-item-header-building">{buildingLocalizationHandler.get(queueItem.data.dataId)}</span>
            <span> </span>
            <span>{localizationHandler.get("level")}</span>
            <span> </span>
            <span className="skyxplore-game-planet-queue-item-header-current-level">{queueItem.data.currentLevel}</span>
            <span>{" => "}</span>
            <span className="skyxplore-game-planet-queue-item-header-target-level">{queueItem.data.currentLevel + 1}</span>
        </div>
    );
}

export default ConstructionHeader;