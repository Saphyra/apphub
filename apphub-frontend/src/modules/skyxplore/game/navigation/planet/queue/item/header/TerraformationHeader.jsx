import React from "react";
import surfaceLocalizationData from "../../../../../common/localization/surface_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";

const TerraformationHeader = ({ localizationHandler, queueItem }) => {
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    return (
        <div className="skyxplore-game-planet-queue-item-header">
            <span>{localizationHandler.get("terraformation")}</span>
            <span>: </span>
            <span>{surfaceLocalizationHandler.get(queueItem.data.currentSurfaceType)}</span>
            <span>{" => "}</span>
            <span>{surfaceLocalizationHandler.get(queueItem.data.targetSurfaceType)}</span>
        </div>
    );
}

export default TerraformationHeader;