import React from "react";

const ConstructionAreaConstructionHeader = ({ constructionArea, localizationHandler }) => {
    //TODO update content
    return (
        <div>
            <span>{localizationHandler.get("level")}</span>
            <span>: </span>
            <span className="skyxplore-planet-surface-header-building-level">{constructionArea.level}</span>
            <span>{" => "}</span>
            <span className="skyxplore-planet-surface-header-building-new-level">{constructionArea.level + 1}</span>
        </div>
    );
}

export default ConstructionAreaConstructionHeader;