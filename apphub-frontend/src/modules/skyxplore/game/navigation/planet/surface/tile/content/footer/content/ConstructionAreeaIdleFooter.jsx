import React from "react";
import Button from "../../../../../../../../../../common/component/input/Button";
import confirmDeconstructConstructionArea from "./DeconstructBuildingService";
import buildingLocalizationData from "../../../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const ConstructionAreeaIdleFooter = ({ localizationHandler, surface, setConfirmationDialogData, planetId }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    return (
        <div>
            <Button
                className="skyxplore-game-planet-surface-building-deconstruct-button"
                label="X"
                title={localizationHandler.get("deconstruct")}
                onclick={() => confirmDeconstructConstructionArea(
                    localizationHandler,
                    buildingLocalizationHandler,
                    surface,
                    setConfirmationDialogData,
                    planetId
                )}
            />
        </div>
    );
}

export default ConstructionAreeaIdleFooter;