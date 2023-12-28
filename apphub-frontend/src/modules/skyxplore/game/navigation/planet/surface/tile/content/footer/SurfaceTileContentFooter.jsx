import React, { useEffect, useState } from "react";
import Utils from "../../../../../../../../../common/js/Utils";
import Button from "../../../../../../../../../common/component/input/Button";
import { useQuery } from "react-query";
import Endpoints from "../../../../../../../../../common/js/dao/dao";
import localizationData from "./surface_tile_content_footer_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import ConfirmationDialogData from "../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import buildingLocalizationData from "../../../../../../common/localization/building_localization.json";
import SurfaceTileContentFooterProgressBar from "./progress_bar/SurfaceTileContentFooterProgressBar";

//TODO split
const SurfaceTileContentFooter = ({ surface, setConfirmationDialogData, planetId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);

    const [maxLevel, setMaxLevel] = useState(0);

    const { data: itemData } = useQuery(
        surface.building.dataId,
        async () => {
            return await Endpoints.SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: surface.building.dataId })
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
            enabled: Utils.hasValue(surface.building)
        }
    );

    useEffect(
        () => {
            if (Utils.hasValue(itemData)) {
                setMaxLevel(itemData.maxLevel);
            }
        },
        [itemData]
    );

    const confirmDeconstructBuilding = () => {
        const dialogData = new ConfirmationDialogData(
            "skyxplore-game-planet-surface-confirm-deconstruct-building",
            localizationHandler.get("confirm-deconstruct-building-title"),
            localizationHandler.get("confirm-deconstruct-building-content", { buildingName: buildingLocalizationHandler.get(surface.building.dataId) }),
            [
                <Button
                    key="deconstruct"
                    id="skyxplore-game-planet-surface-confirm-deconstruct-building-button"
                    label={localizationHandler.get("deconstruct")}
                    onclick={deconstructBuilding}
                />,
                <Button
                    key="cancel"
                    id="skyxplore-game-planet-surface-confirm-deconstruct-building-cancel-button"
                    onclick={() => setConfirmationDialogData(null)}
                    label={localizationHandler.get("cancel")}
                />
            ]
        )

        setConfirmationDialogData(dialogData);
    }

    const deconstructBuilding = async () => {
        await Endpoints.SKYXPLORE_BUILDING_DECONSTRUCT.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
            .send();

        setConfirmationDialogData(null);
    }

    const cancelDeconstruction = () => {
        Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
            .send();
    }

    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            const building = surface.building;

            if (Utils.hasValue(building.construction)) {
                //TODO Construction in progress
            } else if (Utils.hasValue(building.deconstruction)) {
                const deconstruction = building.deconstruction;
                console.log(deconstruction);

                return <SurfaceTileContentFooterProgressBar
                    actual={deconstruction.currentWorkPoints}
                    max={deconstruction.requiredWorkPoints}
                    title={localizationHandler.get("cancel-deconstruction")}
                    cancelCallback={cancelDeconstruction}
                />
            } else {
                return (
                    <div>
                        <Button
                            className="skyxplore-game-planet-surface-building-deconstruct-button"
                            label="X"
                            title={localizationHandler.get("deconstruct")}
                            onclick={confirmDeconstructBuilding}
                        />

                        {isUpgradeAvailable() &&
                            <Button
                                className="skyxplore-game-planet-surface-building-upgrade-button"
                                label="."
                                title={localizationHandler.get("upgrade")}
                                onclick={() => { }} //TODO upgrade building
                            />
                        }
                    </div>
                );
            }
        } else if (Utils.hasValue(surface.terraformation)) {
            //TODO Terraformation in progress
        }
    }

    const isUpgradeAvailable = () => {
        return maxLevel > surface.building.level;
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-footer">
            {getContent()}
        </div>
    );
}

export default SurfaceTileContentFooter;