import React from "react";
import "./surface_tile.css";
import Utils from "../../../../../../../common/js/Utils";
import Button from "../../../../../../../common/component/input/Button";
import localizationData from "./surface_tile_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import SurfaceTileContent from "./content/SurfaceTileContent";
import NavigationHistoryItem from "../../../NavigationHistoryItem";
import PageName from "../../../PageName";

const SurfaceTile = ({ surface, setConfirmationDialogData, planetId, openPage }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const isEmpty = () => {
        return !Utils.hasValue(surface.building) && !Utils.hasValue(surface.terraformation);
    }

    const getContent = () => {
        if (isEmpty()) {
            return <EmptySurfaceFooter
                localizationHandler={localizationHandler}
                planetId={planetId}
                surfaceId={surface.surfaceId}
                surfaceType={surface.surfaceType}
                openPage={openPage}
            />
        } else {
            return <SurfaceTileContent
                surface={surface}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
                openPage={openPage}
            />
        }
    }

    return (
        <div className={"skyxplore-game-planet-surface-tile surface-type-" + surface.surfaceType.toLowerCase()}>
            {getContent()}
        </div>
    );
}

const EmptySurfaceFooter = ({ localizationHandler, planetId, surfaceId, surfaceType, openPage }) => {
    return (
        <div className="skyxplore-game-planet-surface-footer">
            <Button
                className="skyxplore-game-planet-surface-modify-button"
                title={localizationHandler.get("modify-surface")}
                onclick={() => openPage(new NavigationHistoryItem(PageName.MODIFY_SURFACE, { planetId: planetId, surfaceId: surfaceId, surfaceType: surfaceType }))}
            />
        </div>
    );
}

export default SurfaceTile;