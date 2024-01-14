import React from "react"
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelTerraformation from "./CancelTerraformationService";
import surfaceLocalizationData from "../../../../../../../common/localization/surface_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";

const TerraformationFooter = ({ surface, localizationHandler, setConfirmationDialogData, planetId}) => {
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    const terraformation = surface.terraformation;

    return <SurfaceTileContentFooterProgressBar
        actual={terraformation.currentWorkPoints}
        max={terraformation.requiredWorkPoints}
        title={localizationHandler.get("cancel-terraformation")}
        cancelCallback={() => confirmCancelTerraformation(
            localizationHandler,
            surfaceLocalizationHandler,
            surface,
            setConfirmationDialogData,
            planetId
        )}
    />
}

export default TerraformationFooter;