import React from "react";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const ConstructionAreaDefaultHeader = ({  constructionArea }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    return (
        <div>
           {constructionAreaLocalizationHandler.get(constructionArea.dataId)}
        </div>
    );
}

export default ConstructionAreaDefaultHeader;