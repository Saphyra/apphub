import React from "react";
import Utils from "../../../../../../../../../common/js/Utils";

const SurfaceTileContentHeader = ({ surface, localizationHandler }) => {
    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            const building = surface.building;
            if (Utils.hasValue(building.construction)) {
                //TODO Construction in progress
            } else {
                return (
                    <div>
                        <span>{localizationHandler.get("level")}</span>
                        <span>: </span>
                        <span>{building.level}</span>
                    </div>
                )
            }
        } else if (Utils.hasValue(surface.terraformation)) {
            const terraformation = surface.terraformation;
            //TODO terraformation in progress
        } else {
            Utils.throwException("IllegalState", "Surface has no building or terraformation in progress.")
        }
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-header">
            {getContent()}
        </div>
    )
}

export default SurfaceTileContentHeader;