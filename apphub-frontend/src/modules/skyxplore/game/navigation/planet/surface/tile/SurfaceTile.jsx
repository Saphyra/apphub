import React from "react";
import "./surface_tile.css";
import Utils from "../../../../../../../common/js/Utils";
import Button from "../../../../../../../common/component/input/Button";
import localizationData from "./surface_tile_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import SurfaceTileContent from "./content/SurfaceTileContent";

const SurfaceTile = ({ surface }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const isEmpty = () => {
        return !Utils.hasValue(surface.building) && !Utils.hasValue(surface.terraformation);
    }

    const getContent = () => {
        if (isEmpty()) {
            return <EmptySurfaceFooter
                localizationHandler={localizationHandler}
            />
        } else {
            return <SurfaceTileContent
                surface={surface}
            />
        }
    }

    return (
        <div className={"skyxplore-game-planet-surface-tile " + surface.surfaceType.toLowerCase()}>
            {getContent()}
        </div>
    );
}

const EmptySurfaceFooter = ({ localizationHandler }) => {
    return (
        <div className="skyxplore-game-planet-surface-footer">
            <Button
                className="skyxplore-game-planet-surface-modify-button"
                title={localizationHandler.get("modify-surface")}
                onclick={() => { }} //TODO add handler
            />
        </div>
    );
}

export default SurfaceTile;