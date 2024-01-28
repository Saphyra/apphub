import React from "react";
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelConstruction from "./CancelConstructionService";
import buildingLocalizationData from "../../../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const BuildingConstructionFooter = ({ surface, localizationHandler, setConfirmationDialogData, planetId }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    const building = surface.building;
    const construction = building.construction;

    return <SurfaceTileContentFooterProgressBar
        actual={construction.currentWorkPoints}
        max={construction.requiredWorkPoints}
        title={localizationHandler.get("cancel-construction")}
        cancelCallback={() => confirmCancelConstruction(
            localizationHandler,
            buildingLocalizationHandler,
            surface,
            setConfirmationDialogData,
            planetId
        )}
    />
}

export default BuildingConstructionFooter;