import React from "react";
import Utils from "../../../../../../../../../common/js/Utils";
import localizationData from "./surface_tile_content_header_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import surfaceLocalizationData from "../../../../../../common/localization/surface_localization.json";

//TODO split
const SurfaceTileContentHeader = ({ surface }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            const building = surface.building;
            if (Utils.hasValue(building.construction)) {
                return (
                    <div>
                        <span>{localizationHandler.get("level")}</span>
                        <span>: </span>
                        <span className="skyxplore-planet-surface-header-building-level">{building.level}</span>
                        <span>{" => "}</span>
                        <span className="skyxplore-planet-surface-header-building-new-level">{building.level + 1}</span>
                    </div>
                );
            } else if (Utils.hasValue(building.deconstruction)) {
                return (
                    <span className="skyxplore-planet-surface-header-deconstructing">{localizationHandler.get("deconstructing")}</span>
                );
            } else {
                return (
                    <div>
                        <span>{localizationHandler.get("level")}</span>
                        <span>: </span>
                        <span className="skyxplore-planet-surface-header-building-level">{building.level}</span>
                    </div>
                );
            }
        } else if (Utils.hasValue(surface.terraformation)) {
            const terraformation = surface.terraformation;
            return (
                <div>
                    <span>{"=> "}</span>
                    <span className="skyxplore-planet-surface-header-terraformation-target">{surfaceLocalizationHandler.get(terraformation.data)}</span>
                </div>
            )
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