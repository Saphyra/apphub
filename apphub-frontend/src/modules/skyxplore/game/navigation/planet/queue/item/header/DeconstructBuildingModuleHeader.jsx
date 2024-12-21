import React from "react";
import buildingModuleLocalizationdata from "../../../../../common/localization/building_module_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";

const DeconstructBuildingModuleHeader = ({ queueItem, localizationHandler}) => {
    const buildingModuleLocalizationHandler = new LocalizationHandler(buildingModuleLocalizationdata);

    return (
        <div className="skyxplore-game-planet-queue-item-header">
            <span>{localizationHandler.get("deconstruct")}</span>
            <span>: </span>
            <span>{buildingModuleLocalizationHandler.get(queueItem.data.dataId)}</span>
        </div>
    );
}

export default DeconstructBuildingModuleHeader;