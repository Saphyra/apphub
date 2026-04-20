import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./terraforming_possibility_localization.json";
import surfaceLocalizationData from "../../../../common/localization/surface_localization.json";
import ConstructionCost from "modules/feature/skyxplore/game/common/component/construction_cost/ConstructionCost";
import Button from "common/component/input/Button";
import "./terraforming_possibility.css";

const TerraformingPossibility = ({ surfaceType, constructionRequirements, terraformCallback }) => {
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div
            id={"skyxplore-game-terraforming-possibility-" + surfaceType.toLowerCase()}
            className="skyxplore-game-terraforming-possibility"
        >
            <div className="skyxplore-game-terraforming-possibility-header">
                {surfaceLocalizationHandler.get(surfaceType)}
            </div>

            <div className="skyxplore-game-terraforming-possibility-construction-requirements-wrapper">
                <ConstructionCost
                    className="skyxplore-game-terraforming-possibility-construction-requirements"
                    constructionRequirements={constructionRequirements}
                />
            </div>

            <Button
                className="skyxplore-game-terraform-button"
                label={localizationHandler.get("terraform")}
                onclick={terraformCallback}
            />
        </div>
    );
}

export default TerraformingPossibility;