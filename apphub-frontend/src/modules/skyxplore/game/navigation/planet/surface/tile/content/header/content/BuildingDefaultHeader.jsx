import React from "react";

const BuildingDefaultHeader = ({ localizationHandler, building }) => {
    return (
        <div>
            <span>{localizationHandler.get("level")}</span>
            <span>: </span>
            <span className="skyxplore-planet-surface-header-building-level">{building.level}</span>
        </div>
    );
}

export default BuildingDefaultHeader;