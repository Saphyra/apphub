import React from "react";
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelDeconstruction from "./CancelDeconstructionService";
import buildingLocalizationData from "../../../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const ConstructionAreaDeconstructionFooter = ({ surface, localizationHandler, setConfirmationDialogData }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    const constructionArea = surface.constructionArea;
    const deconstruction = constructionArea.deconstruction;

    return <SurfaceTileContentFooterProgressBar
        actual={deconstruction.currentWorkPoints}
        max={deconstruction.requiredWorkPoints}
        title={localizationHandler.get("cancel-deconstruction")}
        cancelCallback={() => confirmCancelDeconstruction(
            localizationHandler,
            constructionArea,
            setConfirmationDialogData
        )}
    />
}

export default ConstructionAreaDeconstructionFooter;