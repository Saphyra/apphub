import React from "react";
import constructionAreaLocalizationData from "../../../../../common/localization/construction_area_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";

const DeconstructConstructionAreaHeader = ({ localizationHandler, queueItem }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    return (
        <div className="skyxplore-game-planet-queue-item-header">
            <span>{localizationHandler.get("deconstruct")}</span>
            <span>: </span>
            <span>{constructionAreaLocalizationHandler.get(queueItem.data.dataId)}</span>
        </div>
    );
}

export default DeconstructConstructionAreaHeader;