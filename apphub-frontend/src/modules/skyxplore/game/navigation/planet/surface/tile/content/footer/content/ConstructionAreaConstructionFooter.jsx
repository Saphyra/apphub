import React from "react";
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelConstructionAreaConstruction from "./CancelConstructionAreaConstructionService";

const ConstructionAreaConstructionFooter = ({ surface, localizationHandler, setConfirmationDialogData }) => {
    const constructionArea = surface.constructionArea;
    const construction = constructionArea.construction;

    return <SurfaceTileContentFooterProgressBar
        actual={construction.currentWorkPoints}
        max={construction.requiredWorkPoints}
        title={localizationHandler.get("cancel-construction-area-construction")}
        cancelCallback={() => confirmCancelConstructionAreaConstruction(
            constructionArea.dataId,
            localizationHandler,
            construction.constructionId,
            setConfirmationDialogData
        )}
    />
}

export default ConstructionAreaConstructionFooter;