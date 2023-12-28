import React from "react";
import "./planet_surface.css";
import Stream from "../../../../../../common/js/collection/Stream";
import SurfaceTile from "./tile/SurfaceTile";

const PlanetSurface = ({ surfaces, setConfirmationDialogData, planetId, openPage }) => {
    const getSurfaces = () => {
        return new Stream(surfaces)
            .sorted((a, b) => {
                if (a.coordinate.x == b.coordinate.x) {
                    return a.coordinate.y - b.coordinate.y;
                }

                return a.coordinate.x - b.coordinate.x;
            })
            .map(surface => <SurfaceTile
                key={surface.surfaceId}
                surface={surface}
                setConfirmationDialogData={setConfirmationDialogData}
                planetId={planetId}
                openPage={openPage}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-planet-surface">
            <div
                id="skyxplore-game-planet-surface-container"
                style={{
                    gridTemplateColumns: "repeat(" + Math.sqrt(surfaces.length) + ", var(--skyxplore-game-planet-surface-tile-size)"
                }}
            >
                {getSurfaces()}
            </div>
        </div>
    );
}

export default PlanetSurface;