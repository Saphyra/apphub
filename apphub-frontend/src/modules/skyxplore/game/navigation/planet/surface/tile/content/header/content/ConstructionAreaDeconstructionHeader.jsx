import React from "react";

const ConstructionAreaDeconstructionHeader = ({ localizationHandler }) => {
    return (
        <span className="skyxplore-planet-surface-header-deconstructing">{localizationHandler.get("deconstructing")}</span>
    );
}

export default ConstructionAreaDeconstructionHeader;