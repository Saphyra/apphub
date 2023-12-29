import React from "react";
import "./terraforming_possibility.css";
import surfaceLocalizationData from "../../../../common/localization/surface_localization.json"
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import ConstructionCost from "../../../../common/component/construction_cost/ConstructionCost";
import Button from "../../../../../../../common/component/input/Button";
import localizationData from "./terraforming_possibility_localization.json";

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