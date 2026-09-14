import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./modify_surface_localization.json";
import "./modify_surface.css";
import Button from "common/component/input/Button";
import ConstructionAreas from "./construction_area/ConstructionAreas";
import TerraformingPossibilities from "./terraformin_possibilities/TerraforminPossibilities";

const ModifySurface = ({ closePage, footer, planetId, surfaceId, surfaceType, setDisplaySpinner }) => {
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
                    setDisplaySpinner={setDisplaySpinner}
                />

                <TerraformingPossibilities
                    surfaceType={surfaceType}
                    planetId={planetId}
                    surfaceId={surfaceId}
                    closePage={closePage}
                    setDisplaySpinner={setDisplaySpinner}
                />
            </main>

            {footer}
        </div>
    );
}

export default ModifySurface;