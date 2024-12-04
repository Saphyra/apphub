import React from "react";
import buildingModuleLocalizationData from "../../../common/localization/building_module_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Button from "../../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_DECONSTRUCT_BUILDING_MODULE } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import { hasValue } from "../../../../../../common/js/Utils";
import BuildingDeconstruction from "./building/BuildingDeconstruction";
import BuildingConstruction from "./building/BuildingConstruction";

const ConstructionAreaSlotBuilding = ({ localizationHandler, building, setBuildings, setConfirmationDialogData }) => {
    const buildingModuleLocalizationHandler = new LocalizationHandler(buildingModuleLocalizationData);

    const openDeconstructionConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "skyxplore-game-construction-area-deconstruct-module-confirmation",
            localizationHandler.get("deconstruct-building-title"),
            localizationHandler.get("deconstruct-building-detail", { building: buildingModuleLocalizationHandler.get(building.dataId) }),
            [
                <Button
                    key="deconstruct"
                    id="skyxplore-game-constuction-area-deconstruct-module-confirm-button"
                    label={localizationHandler.get("deconstruct")}
                    onclick={deconstructBuilding}
                />,
                <Button
                    key="cancel"
                    id="skyxplore-game-constuction-area-deconstruct-module-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deconstructBuilding = async () => {
        const response = await SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_DECONSTRUCT_BUILDING_MODULE.createRequest(null, { buildingModuleId: building.buildingModuleId })
            .send();

        setBuildings(response);
        setConfirmationDialogData(null);
    }

    return (
        <div className={"skyxplore-game-construction-area-slot skyxplore-game-construction-area-slot-building skyxplore-game-construction-area-slot-building-" + building.dataId}>
            <div className="skyxplore-game-construction-area-slot-title">{buildingModuleLocalizationHandler.get(building.dataId)}</div>
            <div className="skyxplore-game-construction-area-slot-content">
                <div className="skyxplore-game-construction-area-slot-content-description">Imagine description here</div>

                {hasValue(building.construction) &&
                    <BuildingConstruction
                        construction={building.construction}
                        localizationHandler={localizationHandler}
                        setBuildings={setBuildings}
                    />
                }

                {hasValue(building.deconstruction) &&
                    <BuildingDeconstruction
                        deconstruction={building.deconstruction}
                        localizationHandler={localizationHandler}
                        setBuildings={setBuildings}
                    />
                }

                <div className="skyxplore-game-construction-area-slot-content-details">Imagine details here</div>

                {!hasValue(building.deconstruction) && !hasValue(building.construction) &&
                    <Button
                        className="skyxplore-game-construction-area-building-module-deconstruct-button"
                        label="X"
                        title={localizationHandler.get("deconstruct")}
                        onclick={openDeconstructionConfirmation}
                    />
                }

            </div>
        </div>
    );
}

export default ConstructionAreaSlotBuilding;