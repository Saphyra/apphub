import React, { useState } from "react";
import Button from "../../../../../../../../../../common/component/input/Button";
import confirmDeconstructBuilding from "./DeconstructBuildingService";
import buildingLocalizationData from "../../../../../../../common/localization/building_localization.json";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";
import useCache from "../../../../../../../../../../common/hook/Cache";
import NavigationHistoryItem from "../../../../../../NavigationHistoryItem";
import PageName from "../../../../../../PageName";
import { SKYXPLORE_GET_ITEM_DATA } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";

const BuildingIdleFooter = ({ localizationHandler, surface, setConfirmationDialogData, planetId, openPage }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    const building = surface.building;

    const [maxLevel, setMaxLevel] = useState(0);

    useCache(
        building.dataId,
        SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: building.dataId }),
        (buildingData) => setMaxLevel(buildingData.maxLevel)
    )

    const isUpgradeAvailable = () => {
        return maxLevel > surface.building.level;
    }

    return (
        <div>
            <Button
                className="skyxplore-game-planet-surface-building-deconstruct-button"
                label="X"
                title={localizationHandler.get("deconstruct")}
                onclick={() => confirmDeconstructBuilding(
                    localizationHandler,
                    buildingLocalizationHandler,
                    surface,
                    setConfirmationDialogData,
                    planetId
                )}
            />

            {isUpgradeAvailable() &&
                <Button
                    className="skyxplore-game-planet-surface-building-upgrade-button"
                    label="."
                    title={localizationHandler.get("upgrade")}
                    onclick={() => openPage(
                        new NavigationHistoryItem(
                            PageName.UPGRADE_BUILDING,
                            {
                                dataId: building.dataId,
                                currentLevel: building.level,
                                planetId: planetId,
                                buildingId: building.buildingId,
                                surfaceType: surface.surfaceType
                            }
                        )
                    )}
                />
            }
        </div>
    );
}

export default BuildingIdleFooter;