import React from "react";
import Utils from "../../../../../../../../../common/js/Utils";
import localizationData from "./surface_tile_content_header_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import TerraformationHeader from "./content/TerraformationHeader";
import BuildingConstructionHeader from "./content/BuildingConstructionHeader";
import BuildingDeconstructionHeader from "./content/BuildingDeconstructionHeader";
import BuildingDefaultHeader from "./content/BuildingDefaultHeader";

const SurfaceTileContentHeader = ({ surface }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            const building = surface.building;
            if (Utils.hasValue(building.construction)) {
                return <BuildingConstructionHeader
                    localizationHandler={localizationHandler}
                    building={building}
                />
            } else if (Utils.hasValue(building.deconstruction)) {
                return <BuildingDeconstructionHeader
                    localizationHandler={localizationHandler}
                />
            } else {
                return <BuildingDefaultHeader
                    localizationHandler={localizationHandler}
                    building={building}
                />
            }
        } else if (Utils.hasValue(surface.terraformation)) {
            return <TerraformationHeader
                terraformation={surface.terraformation}
            />
        } else {
            Utils.throwException("IllegalState", "Surface has no building or terraformation in progress. It should be an empty surface.")
        }
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-header">
            {getContent()}
        </div>
    )
}

export default SurfaceTileContentHeader;