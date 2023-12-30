import React, { useEffect, useState } from "react";
import Utils from "../../../../../../../../../common/js/Utils";
import Button from "../../../../../../../../../common/component/input/Button";
import { useQuery } from "react-query";
import Endpoints from "../../../../../../../../../common/js/dao/dao";
import localizationData from "./surface_tile_content_footer_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import ConfirmationDialogData from "../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import buildingLocalizationData from "../../../../../../common/localization/building_localization.json";
import surfaceLocalizationData from "../../../../../../common/localization/surface_localization.json";
import SurfaceTileContentFooterProgressBar from "./progress_bar/SurfaceTileContentFooterProgressBar";
import NavigationHistoryItem from "../../../../../NavigationHistoryItem";
import PageName from "../../../../../PageName";

//TODO split
const SurfaceTileContentFooter = ({ surface, setConfirmationDialogData, planetId, openPage }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    const [maxLevel, setMaxLevel] = useState(0);

    const { data: itemData } = useQuery(
        Utils.hasValue(surface.building) ? surface.building.dataId : null,
        async () => {
            const dataId = Utils.hasValue(surface.building) ? surface.building.dataId : null;
            return await Endpoints.SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: dataId })
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

    const confirmCancelDeconstruction = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-planet-confirm-cancel-deconstruction",
            localizationHandler.get("cancel-deconstruction-title"),
            localizationHandler.get("cancel-deconstruction-content", { buildingName: buildingLocalizationHandler.get(surface.building.dataId) }),
            [
                <Button
                    key="cancel-cdeonstruction"
                    id="skyxplore-game-planet-cancel-deconstruction-button"
                    label={localizationHandler.get("cancel-deconstruction")}
                    onclick={cancelDeconstruction}
                />,
                <Button
                    key="continue-construction"
                    id="skyxplore-game-planet-continue-deconstruction-button"
                    label={localizationHandler.get("continue-deconstruction")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );

        setConfirmationDialogData(confirmationDialogData);
    }

    const cancelDeconstruction = async () => {
        await Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
            .send();

        setConfirmationDialogData(null);
    }

    const confirmCancelConstruction = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-planet-confirm-cancel-construction",
            localizationHandler.get("cancel-construction-title"),
            localizationHandler.get("cancel-construction-content", { buildingName: buildingLocalizationHandler.get(surface.building.dataId) }),
            [
                <Button
                    key="cancel-construction"
                    id="skyxplore-game-planet-cancel-construction-button"
                    label={localizationHandler.get("cancel-construction")}
                    onclick={cancelConstruction}
                />,
                <Button
                    key="continue-construction"
                    id="skyxplore-game-planet-continue-construction-button"
                    label={localizationHandler.get("continue-construction")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );

        setConfirmationDialogData(confirmationDialogData);
    }

    const cancelConstruction = async () => {
        await Endpoints.SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
            .send();

        setConfirmationDialogData(null);
    }

    const confirmCancelTerraformation = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-planet-confirm-cancel-terraformation",
            localizationHandler.get("cancel-terraformation-title"),
            localizationHandler.get("cancel-terraformation-content", { surfaceType: surfaceLocalizationHandler.get(surface.terraformation.data) }),
            [
                <Button
                    key="cancel"
                    id="skyxplore-game-planet-cancel-terraformation-button"
                    label={localizationHandler.get("cancel-terraformation")}
                    onclick={cancelTerraformation}
                />,
                <Button
                    key="continue"
                    id="skyxplore-game-planet-continue-terraformation-button"
                    label={localizationHandler.get("continue-terraformation")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );

        setConfirmationDialogData(confirmationDialogData);
    }

    const cancelTerraformation = async () => {
        await Endpoints.SKYXPLORE_GAME_CANCEL_TERRAFORMATION.createRequest(null, { planetId: planetId, surfaceId: surface.surfaceId })
            .send();

        setConfirmationDialogData(null);
    }

    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            const building = surface.building;

            if (Utils.hasValue(building.construction)) {
                const construction = building.construction;

                return <SurfaceTileContentFooterProgressBar
                    actual={construction.currentWorkPoints}
                    max={construction.requiredWorkPoints}
                    title={localizationHandler.get("cancel-construction")}
                    cancelCallback={confirmCancelConstruction}
                />
            } else if (Utils.hasValue(building.deconstruction)) {
                const deconstruction = building.deconstruction;

                return <SurfaceTileContentFooterProgressBar
                    actual={deconstruction.currentWorkPoints}
                    max={deconstruction.requiredWorkPoints}
                    title={localizationHandler.get("cancel-deconstruction")}
                    cancelCallback={confirmCancelDeconstruction}
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
        } else if (Utils.hasValue(surface.terraformation)) {
            const terraformation = surface.terraformation;

            return <SurfaceTileContentFooterProgressBar
                actual={terraformation.currentWorkPoints}
                max={terraformation.requiredWorkPoints}
                title={localizationHandler.get("cancel-terraformation")}
                cancelCallback={confirmCancelTerraformation}
            />
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