import { hasValue } from "common/js/Utils";
import SurfaceTileContentFooter from "./footer/SurfaceTileContentFooter";
import SurfaceTileContentHeader from "./header/SurfaceTileContentHeader";
import "./surface_tile_content.css";

const SurfaceTileContent = ({ surface, setConfirmationDialogData, planetId, openPage, setDisplaySpinner }) => {
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
                setDisplaySpinner={setDisplaySpinner}
            />
        </div>
    );
}

export default SurfaceTileContent;