import React from "react";
import "./surface_tile_content.css";
import SurfaceTileContentHeader from "./header/SurfaceTileContentHeader";
import SurfaceTileContentFooter from "./footer/SurfaceTileContentFooter";
import { hasValue } from "../../../../../../../../common/js/Utils";

const SurfaceTileContent = ({ surface, setConfirmationDialogData, planetId, openPage }) => {
    return (
        <div className={"skyxplore-game-planet-surface-tile-content" + (hasValue(surface.constructionArea) ? " construction-area-" + surface.constructionArea.dataId : "")}>
            <SurfaceTileContentHeader
                surface={surface}
            />

            <SurfaceTileContentFooter
                surface={surface}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
                openPage={openPage}
            />
        </div>
    );
}

export default SurfaceTileContent;