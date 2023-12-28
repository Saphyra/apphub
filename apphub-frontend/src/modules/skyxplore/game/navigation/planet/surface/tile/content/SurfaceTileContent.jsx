import React from "react";
import "./surface_tile_content.css";
import Utils from "../../../../../../../../common/js/Utils";
import SurfaceTileContentHeader from "./header/SurfaceTileContentHeader";
import SurfaceTileContentFooter from "./footer/SurfaceTileContentFooter";

const SurfaceTileContent = ({ surface, setConfirmationDialogData, planetId }) => {
    //TODO add building description
    return (
        <div className={"skyxplore-game-planet-surface-tile-content" + (Utils.hasValue(surface.building) ? " " + surface.building.dataId : "")}>
            <SurfaceTileContentHeader
                surface={surface}
            />

            <SurfaceTileContentFooter
                surface={surface}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
            />
        </div>
    );
}

export default SurfaceTileContent;