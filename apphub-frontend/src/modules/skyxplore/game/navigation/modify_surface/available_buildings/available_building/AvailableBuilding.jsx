import React, { useEffect, useState } from "react";
import "./available_building.css";
import buildingLocalizationData from "../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import itemDescriptionLocalizationData from "../../../../common/localization/item_description.json";
import BuildingEffectTable from "../../../../common/component/building_effect/BuildingEffectTable";
import Endpoints from "../../../../../../../common/js/dao/dao";
import { useQuery } from "react-query";
import ConstructionCost from "../../../../common/component/construction_cost/ConstructionCost";
import Button from "../../../../../../../common/component/input/Button";
import localizationData from "./available_building_localization.json";
import { hasValue } from "../../../../../../../common/js/Utils";

const AvailableBuilding = ({ buildingDataId, surfaceType, constructCallback }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);
    const itemDescriptionLocalizationHandler = new LocalizationHandler(itemDescriptionLocalizationData);
    const localizationHandler = new LocalizationHandler(localizationData);

    const [buildingData, setBuildingData] = useState(null);

    const { data } = useQuery(
        buildingDataId,
        async () => {
            return await Endpoints.SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: buildingDataId })
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
        }
    );

    useEffect(
        () => {
            if (hasValue(data)) {
                setBuildingData(data);
            }
        },
        [data]
    );


    return (
        <div
            id={"skyxplore-game-available-building-" + buildingDataId}
            className="skyxplore-game-available-building"
        >
            <div className="skyxplore-game-available-building-header">
                {buildingLocalizationHandler.get(buildingDataId)}
            </div>

            <div className="skyxplore-game-available-building-description">
                {itemDescriptionLocalizationHandler.get(buildingDataId)}
            </div>

            <div className="skyxplore-game-available-building-table-wrapper">

                {hasValue(buildingData) &&
                    <BuildingEffectTable
                        className="skyxplore-game-available-building-effect"
                        surfaceType={surfaceType}
                        itemData={buildingData}
                        currentLevel={1}
                    />
                }

                {hasValue(buildingData) &&
                    <ConstructionCost
                        className="skyxplore-game-available-building-construction-requirements"
                        constructionRequirements={buildingData.constructionRequirements[1]}
                    />
                }

            </div>

            <Button
                className="skyxplore-game-construct-new-building-button"
                label={localizationHandler.get("construct")}
                onclick={constructCallback}
            />
        </div>
    );
}

export default AvailableBuilding;