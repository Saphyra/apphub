import React from "react";
import Button from "../../../../../../../../../../common/component/input/Button";
import confirmDeconstructConstructionArea from "./DeconstructConstructionAreaService";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";
import NavigationHistoryItem from "../../../../../../NavigationHistoryItem";
import PageName from "../../../../../../PageName";

const ConstructionAreeaIdleFooter = ({ localizationHandler, constructionArea, setConfirmationDialogData, openPage }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    return (
        <div>
            <Button
                className="skyxplore-game-planet-surface-construction-area-deconstruct-button"
                label="X"
                title={localizationHandler.get("deconstruct")}
                onclick={() => confirmDeconstructConstructionArea(
                    localizationHandler,
                    constructionAreaLocalizationHandler,
                    constructionArea,
                    setConfirmationDialogData
                )}
            />
            <Button
                className="skyxplore-game-planet-surface-construction-area-open-button"
                label=""
                onclick={() => openPage(new NavigationHistoryItem(PageName.CONSTRUCTION_AREA, { constructionArea: constructionArea }))}
            />
        </div>
    );
}

export default ConstructionAreeaIdleFooter;