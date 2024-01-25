import React from "react";

const BuildingDeconstructionHeader = ({ localizationHandler }) => {
    return (
        <span className="skyxplore-planet-surface-header-deconstructing">{localizationHandler.get("deconstructing")}</span>
    );
}

export default BuildingDeconstructionHeader;