import React from "react";
import localizationData from "./surface_tile_content_footer_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import BuildingFooter from "./content/BuildingFooter";
import TerraformationFooter from "./content/TerraformationFooter";
import { hasValue, throwException } from "../../../../../../../../../common/js/Utils";

const SurfaceTileContentFooter = ({ surface, setConfirmationDialogData, planetId, openPage }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        if (hasValue(surface.building)) {
            return <BuildingFooter
                surface={surface}
                localizationHandler={localizationHandler}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
                openPage={openPage}
            />
        } else if (hasValue(surface.terraformation)) {
            return <TerraformationFooter
                surface={surface}
                localizationHandler={localizationHandler}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
            />
        } else {
            throwException("IllegalState", "Surface has no building or terraformation in progress. It should be an empty surface.");
        }
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-footer">
            {getContent()}
        </div>
    );
}

export default SurfaceTileContentFooter;