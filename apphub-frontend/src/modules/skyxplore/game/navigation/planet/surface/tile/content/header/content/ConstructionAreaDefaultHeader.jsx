import React from "react";

const ConstructionAreaDefaultHeader = ({ localizationHandler, constructionArea }) => {
    //TODO update content
    return (
        <div>
            <span>{localizationHandler.get("level")}</span>
            <span>: </span>
            <span className="skyxplore-planet-surface-header-building-level">{constructionArea.level}</span>
        </div>
    );
}

export default ConstructionAreaDefaultHeader;