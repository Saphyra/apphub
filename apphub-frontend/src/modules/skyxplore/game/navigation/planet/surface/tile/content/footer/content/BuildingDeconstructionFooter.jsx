import React from "react";
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelDeconstruction from "./CancelDeconstructionService";
import buildingLocalizationData from "../../../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const BuildingDeconstructionFooter = ({ surface, localizationHandler, setConfirmationDialogData, planetId }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    const building = surface.building;
    const deconstruction = building.deconstruction;

    return <SurfaceTileContentFooterProgressBar
        actual={deconstruction.currentWorkPoints}
        max={deconstruction.requiredWorkPoints}
        title={localizationHandler.get("cancel-deconstruction")}
        cancelCallback={() => confirmCancelDeconstruction(
            localizationHandler,
            buildingLocalizationHandler,
            surface,
            setConfirmationDialogData,
            planetId
        )}
    />
}

export default BuildingDeconstructionFooter;