import React, { useEffect, useState } from "react";
import localizationData from "./surface_tile_content_localization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";
import "./surface_tile_content.css";
import Utils from "../../../../../../../../common/js/Utils";
import SurfaceTileContentHeader from "./header/SurfaceTileContentHeader";
import SurfaceTileContentFooter from "./footer/SurfaceTileContentFooter";

const SurfaceTileContent = ({ surface }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    //TODO add building description
    return (
        <div className={"skyxplore-game-planet-surface-tile-content" + (Utils.hasValue(surface.building) ? " " + surface.building.dataId : "")}>
            <SurfaceTileContentHeader
                surface={surface}
                localizationHandler={localizationHandler}
            />
            
            <SurfaceTileContentFooter
                surface={surface}
                localizationHandler={localizationHandler}
            />
        </div>
    );
}

const SurfaceFooter = ({ content }) => {

}

export default SurfaceTileContent;