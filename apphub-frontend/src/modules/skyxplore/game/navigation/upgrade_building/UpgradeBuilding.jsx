import React, { useEffect, useState } from "react";
import localizationData from "./upgrade_building_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import "./upgrade_building.css";
import buildingLocalizationData from "../../common/localization/building_localization.json";
import itemDescriptionData from "../../common/localization/item_description.json";
import { useQuery } from "react-query";
import Endpoints from "../../../../../common/js/dao/dao";
import BuildingEffectTable from "../../common/component/building_effect/BuildingEffectTable";
import ConstructionCost from "../../common/component/construction_cost/ConstructionCost";
import { hasValue } from "../../../../../common/js/Utils";

const UpgradeBuilding = ({ closePage, footer, dataId, currentLevel, surfaceType, planetId, buildingId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);
    const itemDesctiptionLocalizationHandler = new LocalizationHandler(itemDescriptionData);

    const [itemData, setItemData] = useState(null);

    const { data } = useQuery(
        dataId,
        async () => {
            return await Endpoints.SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: dataId })
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
                setItemData(data);
            }
        },
        [data]
    );

    const upgradeBuilding = async () => {
        await Endpoints.SKYXPLORE_BUILDING_UPGRADE.createRequest(null, { planetId: planetId, buildingId: buildingId })
            .send();

        closePage();
    }

    return (
        <div >
            <header id="skyxplore-game-upgrade-building-header">
                <h1>{localizationHandler.get("title")}</h1>

                <Button
                    id="skyxplore-game-upgrade-building-close-button"
                    className="skyxplore-game-window-close-button"
                    label="X"
                    onclick={closePage}
                />
            </header>

            <main id="skyxplore-game-upgrade-building">
                <div id="skyxplore-game-upgrade-building-container">
                    <div id="skyxplore-game-upgrade-building-container-header">
                        <span>{buildingLocalizationHandler.get(dataId)}</span>
                        <span> </span>
                        <span>{currentLevel}</span>
                        <span>{" => "}</span>
                        <span>{currentLevel + 1}</span>
                    </div>

                    <div id="skyxplore-game-upgrade-building-description">
                        {itemDesctiptionLocalizationHandler.get(dataId)}
                    </div>

                    {hasValue(itemData) &&
                        <BuildingEffectTable
                            id="skyxplore-game-upgrade-building-effect"
                            itemData={itemData}
                            surfaceType={surfaceType}
                            currentLevel={currentLevel}
                        />
                    }

                    {hasValue(itemData) &&
                        <ConstructionCost
                            id="skyxplore-game-upgrade-building-construction-cost"
                            constructionRequirements={itemData.constructionRequirements[currentLevel + 1]}
                        />
                    }

                    <Button
                        id="skyxplore-game-upgrade-building-button"
                        label={localizationHandler.get("upgrade")}
                        onclick={upgradeBuilding}
                    />
                </div>
            </main>

            {footer}
        </div>
    );
}

export default UpgradeBuilding;