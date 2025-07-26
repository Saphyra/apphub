import React, { useState } from "react";
import buildingModuleCategoryLocalizationData from "../../common/localization/building_module_category_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import localizationData from "./construct_building_module_localization.json";
import Button from "../../../../../common/component/input/Button";
import useLoader from "../../../../../common/hook/Loader";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_AVAILABLE_BUILDING_MODULES } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import Stream from "../../../../../common/js/collection/Stream";
import buildingModuleLocalizationData from "../../common/localization/building_module_localization.json";
import AvailableBuilding from "./AvailableBuilding";
import "./construct_building_module.css";

const ConstructBuildingModule = ({ closePage, footer, constructionAreaId, buildingModuleCategory }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingModuleCategoryLocalizationHandler = new LocalizationHandler(buildingModuleCategoryLocalizationData);
    const buildingModuleLocalizationHandler = new LocalizationHandler(buildingModuleLocalizationData);

    const [availableBuildings, setAvailableBuildings] = useState([]);

    useLoader({
        request: SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_AVAILABLE_BUILDING_MODULES.createRequest(null, { constructionAreaId: constructionAreaId, buildingModuleCategory: buildingModuleCategory }),
        mapper: setAvailableBuildings
    });

    const getContent = () => {
        return new Stream(availableBuildings)
            .sorted((a, b) => buildingModuleLocalizationHandler.get(a).localeCompare(buildingModuleLocalizationHandler.get(b)))
            .map(dataId =>
                <AvailableBuilding
                    key={dataId}
                    closePage={closePage}
                    localizationHandler={localizationHandler}
                    dataId={dataId}
                    constructionAreaId={constructionAreaId}
                />
            )
            .toList();
    }

    return (
        <div>
            <header id="skyxplore-game-construct-building-module-header">
                <h1>{localizationHandler.get("title", { buildingModuleCategory: buildingModuleCategoryLocalizationHandler.get(buildingModuleCategory) })}</h1>

                <Button
                    id="skyxplore-game-construct-building-module-close-button"
                    className="skyxplore-game-window-close-button"
                    label="X"
                    onclick={closePage}
                />
            </header>

            <main id="skyxplore-game-construct-building-module">
                {getContent()}
            </main>

            {footer}
        </div>
    );
}

export default ConstructBuildingModule;