import React from "react";
import localizationData from "./surface_tile_content_header_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import TerraformationHeader from "./content/TerraformationHeader";
import ConstructionAreaConstructionHeader from "./content/ConstructionAreaConstructionHeader";
import ConstructionAreaDeconstructionHeader from "./content/ConstructionAreaDeconstructionHeader";
import ConstructionAreaDefaultHeader from "./content/ConstructionAreaDefaultHeader";
import { hasValue, throwException } from "../../../../../../../../../common/js/Utils";

const SurfaceTileContentHeader = ({ surface }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        if (hasValue(surface.constructionArea)) {
            const constructionArea = surface.constructionArea;
            if (hasValue(constructionArea.construction)) {
                return <ConstructionAreaConstructionHeader
                    localizationHandler={localizationHandler}
                    constructionArea={constructionArea}
                />
            } else if (hasValue(constructionArea.deconstruction)) {
                return <ConstructionAreaDeconstructionHeader
                    localizationHandler={localizationHandler}
                />
            } else {
                return <ConstructionAreaDefaultHeader
                    localizationHandler={localizationHandler}
                    constructionArea={constructionArea}
                />
            }
        } else if (hasValue(surface.terraformation)) {
            return <TerraformationHeader
                terraformation={surface.terraformation}
            />
        } else {
            console.log(surface);
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