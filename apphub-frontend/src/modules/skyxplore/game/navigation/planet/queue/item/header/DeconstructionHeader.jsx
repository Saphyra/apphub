import React from "react";
import buildingLocalizationData from "../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";

const DeconstructionHeader = ({queueItem, localizationHandler }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    return (
        <div className="skyxplore-game-planet-queue-item-header">
            <span>{localizationHandler.get("deconstruct")}</span>
            <span>: </span>
            <span className="skyxplore-game-planet-queue-item-header-building">{buildingLocalizationHandler.get(queueItem.data.dataId)}</span>
        </div>
    );
}

export default DeconstructionHeader;