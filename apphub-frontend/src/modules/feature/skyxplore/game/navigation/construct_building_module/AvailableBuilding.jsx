import LocalizationHandler from "common/js/LocalizationHandler";
import buildingModuleLocalizationData from "../../common/localization/building_module_localization.json";
import { useState } from "react";
import useCache from "common/hook/Cache";
import { SKYXPLORE_GET_ITEM_DATA } from "common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CONSTRUCT_BUILDING_MODULE } from "common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import { hasValue } from "common/js/Utils";
import ConstructionCost from "../../common/component/construction_cost/ConstructionCost";
import Button from "common/component/input/Button";

const AvailableBuilding = ({ closePage, localizationHandler, dataId, constructionAreaId }) => {
    const buildingModuleLocalizationHandler = new LocalizationHandler(buildingModuleLocalizationData);

    const [buildingModuleData, setBuildingModuleData] = useState(null);

    useCache(
        dataId,
        SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: dataId }),
        setBuildingModuleData
    );

    const constructBuilding = async () => {
        await SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CONSTRUCT_BUILDING_MODULE.createRequest({ value: dataId }, { constructionAreaId: constructionAreaId })
            .send();

        closePage();
    }

    if (!hasValue(buildingModuleData)) {
        return;
    }

    return (
        <div className={"skyxplore-game-construct-building-module-available-building skyxplore-game-construct-building-module-available-building-" + dataId}>
            <div className="skyxplore-game-construct-building-module-available-building-title">{buildingModuleLocalizationHandler.get(dataId)}</div>

            <div>Imagine description here</div>
            <div>Imagine effects here</div>
            <ConstructionCost
                constructionRequirements={buildingModuleData.constructionRequirements}
            />

            <Button
                className={"skyxplore-game-construct-building-module-button"}
                label={localizationHandler.get("construct")}
                onclick={constructBuilding}
            />
        </div>
    );
}

export default AvailableBuilding;