import React from "react";
import "./surface_tile_content.css";
import Utils from "../../../../../../../../common/js/Utils";
import SurfaceTileContentHeader from "./header/SurfaceTileContentHeader";
import SurfaceTileContentFooter from "./footer/SurfaceTileContentFooter";

const SurfaceTileContent = ({ surface, setConfirmationDialogData, planetId, openPage }) => {
    return (
        <div className={"skyxplore-game-planet-surface-tile-content" + (Utils.hasValue(surface.building) ? " building-" + surface.building.dataId : "")}>
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