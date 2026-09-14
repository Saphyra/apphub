import LocalizationHandler from "common/js/LocalizationHandler";
import surfaceLocalizationData from "../../../../../../../common/localization/surface_localization.json";
import SurfaceTileContentFooterProgressBar from "../progress_bar/SurfaceTileContentFooterProgressBar";
import confirmCancelTerraformation from "./CancelTerraformationService";

const TerraformationFooter = ({ surface, localizationHandler, setConfirmationDialogData, planetId, setDisplaySpinner }) => {
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
            planetId,
            setDisplaySpinner
        )}
    />
}

export default TerraformationFooter;