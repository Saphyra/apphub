import React from "react";
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelDeconstruction from "./CancelDeconstructionService";

const ConstructionAreaDeconstructionFooter = ({ surface, localizationHandler, setConfirmationDialogData }) => {
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