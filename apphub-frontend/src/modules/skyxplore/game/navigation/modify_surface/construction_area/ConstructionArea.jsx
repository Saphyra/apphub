import React from "react";
import constructionAreaLocalizationData from "../../../common/localization/construction_area_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import ConstructionCost from "../../../common/component/construction_cost/ConstructionCost";
import ConstructionAreaSlots from "./ConstructionAreaSlots";
import Button from "../../../../../../common/component/input/Button";
import localizationData from "./construction_area_localization.json";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCT_CONSTRUCTION_AREA } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const ConstructionArea = ({ constructionArea, surfaceId, closePage }) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);
    const localizationHandler = new LocalizationHandler(localizationData);

    const construct = async () => {
        await SKYXPLORE_PLANET_SURFACE_CONSTRUCT_CONSTRUCTION_AREA.createRequest({ value: constructionArea.id }, { surfaceId: surfaceId })
            .send();

        closePage();
    }

    return (
        <div
            id={"skyxplore-game-modify-surface-construction-area-" + constructionArea.id}
            className="skyxplore-game-modify-surface-construction-area"
        >
            <div className="skyxplore-game-modify-surface-construction-area-header">
                {constructionAreaLocalizationHandler.get(constructionArea.id)}
            </div>

            <div className="skyxplore-game-modify-surface-construction-area-slots-wrapper">
                <ConstructionAreaSlots
                    slots={constructionArea.slots}
                />
            </div>

            <div className="skyxplore-game-modify-surface-construction-area-construction-requirements-wrapper">
                <ConstructionCost
                    className="skyxplore-game-modify-surface-construction-area-construction-requirements"
                    constructionRequirements={constructionArea.constructionRequirements}
                />
            </div>

            <Button
                className="skyxplore-game-modify-surface-construct-construction-area-button"
                label={localizationHandler.get("construct")}
                onclick={construct}
            />
        </div>
    );
}

export default ConstructionArea;