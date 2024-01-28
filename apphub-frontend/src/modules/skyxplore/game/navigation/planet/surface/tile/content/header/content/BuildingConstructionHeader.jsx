import React from "react";

const BuildingConstructionHeader =({building, localizationHandler})=> {
    return (
        <div>
            <span>{localizationHandler.get("level")}</span>
            <span>: </span>
            <span className="skyxplore-planet-surface-header-building-level">{building.level}</span>
            <span>{" => "}</span>
            <span className="skyxplore-planet-surface-header-building-new-level">{building.level + 1}</span>
        </div>
    );
}

export default BuildingConstructionHeader;