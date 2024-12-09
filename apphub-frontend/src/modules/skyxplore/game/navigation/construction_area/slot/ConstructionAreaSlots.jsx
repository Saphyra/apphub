import React from "react";
import "./construction_area_slots.css";
import buildingModuleCategoryLocalizationData from "../../../common/localization/building_module_category_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../common/js/collection/Stream";
import ConstructionAreaSlotBuilding from "./ConstructionAreaSlotBuilding";
import ConstructionAreaSlotEmpty from "./ConstructionAreaSlotEmpty";
import localizationData from "./construction_area_slots_localization.json";

const ConstructionAreaSlots = ({ openPage, buildingModuleCategory, amount, constructionArea, buildings, setBuildings, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingModuleCategoryLocalizationHandler = new LocalizationHandler(buildingModuleCategoryLocalizationData);

    const getSlots = () => {
        const result = [];

        new Stream(buildings)
            .map(building =>
                <ConstructionAreaSlotBuilding
                    key={building.buildingModuleId}
                    localizationHandler={localizationHandler}
                    building={building}
                    setBuildings={setBuildings}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            )
            .forEach(building => result.push(building));

        while (result.length < amount) {
            result.push(
                <ConstructionAreaSlotEmpty
                    key={result.length}
                    localizationHandler={localizationHandler}
                    openPage={openPage}
                    constructionAreaId={constructionArea.constructionAreaId}
                    buildingModuleCategory={buildingModuleCategory}
                />)
        }

        return result;
    }

    return (
        <fieldset className={"skyxplore-game-construction-area-slots skyxplore-game-construction-area-slots-" + buildingModuleCategory}>
            <legend>{buildingModuleCategoryLocalizationHandler.get(buildingModuleCategory)}</legend>

            {getSlots()}
        </fieldset>
    );
}

export default ConstructionAreaSlots;