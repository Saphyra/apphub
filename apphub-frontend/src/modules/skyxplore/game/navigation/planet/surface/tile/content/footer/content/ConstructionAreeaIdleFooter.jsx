import React from "react";
import Button from "../../../../../../../../../../common/component/input/Button";
import confirmDeconstructConstructionArea from "./DeconstructConstructionAreaService";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const ConstructionAreeaIdleFooter = ({ localizationHandler, surface, setConfirmationDialogData }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    return (
        <div>
            <Button
                className="skyxplore-game-planet-surface-building-deconstruct-button"
                label="X"
                title={localizationHandler.get("deconstruct")}
                onclick={() => confirmDeconstructConstructionArea(
                    localizationHandler,
                    constructionAreaLocalizationHandler,
                    surface.constructionArea,
                    setConfirmationDialogData
                )}
            />
        </div>
    );
}

export default ConstructionAreeaIdleFooter;