import React from "react";
import localizationData from "./surface_tile_content_header_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import TerraformationHeader from "./content/TerraformationHeader";
import BuildingConstructionHeader from "./content/BuildingConstructionHeader";
import BuildingDeconstructionHeader from "./content/BuildingDeconstructionHeader";
import BuildingDefaultHeader from "./content/BuildingDefaultHeader";
import { hasValue, throwException } from "../../../../../../../../../common/js/Utils";

const SurfaceTileContentHeader = ({ surface }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        if (hasValue(surface.building)) {
            const building = surface.building;
            if (hasValue(building.construction)) {
                return <BuildingConstructionHeader
                    localizationHandler={localizationHandler}
                    building={building}
                />
            } else if (hasValue(building.deconstruction)) {
                return <BuildingDeconstructionHeader
                    localizationHandler={localizationHandler}
                />
            } else {
                return <BuildingDefaultHeader
                    localizationHandler={localizationHandler}
                    building={building}
                />
            }
        } else if (hasValue(surface.terraformation)) {
            return <TerraformationHeader
                terraformation={surface.terraformation}
            />
        } else {
            throwException("IllegalState", "Surface has no building or terraformation in progress. It should be an empty surface.")
        }
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-header">
            {getContent()}
        </div>
    )
}

export default SurfaceTileContentHeader;