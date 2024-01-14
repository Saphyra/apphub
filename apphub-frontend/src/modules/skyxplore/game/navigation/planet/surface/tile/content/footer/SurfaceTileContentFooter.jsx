import React from "react";
import Utils from "../../../../../../../../../common/js/Utils";
import localizationData from "./surface_tile_content_footer_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import BuildingFooter from "./content/BuildingFooter";
import TerraformationFooter from "./content/TerraformationFooter";

const SurfaceTileContentFooter = ({ surface, setConfirmationDialogData, planetId, openPage }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            return <BuildingFooter
                surface={surface}
                localizationHandler={localizationHandler}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
                openPage={openPage}
            />
        } else if (Utils.hasValue(surface.terraformation)) {
            return <TerraformationFooter
                surface={surface}
                localizationHandler={localizationHandler}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
            />
        } else {
            Utils.throwException("IllegalState", "Surface has no building or terraformation in progress. It should be an empty surface.");
        }
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-footer">
            {getContent()}
        </div>
    );
}

export default SurfaceTileContentFooter;