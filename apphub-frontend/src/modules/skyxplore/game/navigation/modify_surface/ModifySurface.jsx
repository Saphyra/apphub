import React from "react";
import localizationData from "./modify_surface_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import "./modify_surface.css";
import TerraformingPossibilities from "./terraformin_possibilities/TerraforminPossibilities";
import ConstructionAreas from "./construction_area/ConstructionAreas";

const ModifySurface = ({ closePage, footer, planetId, surfaceId, surfaceType }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div>
            <header id="skyxplore-game-modify-surface-header">
                <h1>{localizationHandler.get("title")}</h1>

                <Button
                    id="skyxplore-game-modify-surface-close-button"
                    className="skyxplore-game-window-close-button"
                    label="X"
                    onclick={closePage}
                />
            </header>

            <main id="skyxplore-game-modify-surface">
                <ConstructionAreas
                    surfaceType={surfaceType}
                    surfaceId={surfaceId}
                    closePage={closePage}
                />

                <TerraformingPossibilities
                    surfaceType={surfaceType}
                    planetId={planetId}
                    surfaceId={surfaceId}
                    closePage={closePage}
                />
            </main>

            {footer}
        </div>
    );
}

export default ModifySurface;