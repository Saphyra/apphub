import React from "react";
import localizationData from "./construction_area_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../common/js/collection/MapStream";
import buildingModuleCategoryLocalizationData from "../../../common/localization/building_module_category_localization.json";

const ConstructionAreaSlots = ({ slots }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingModuleCategoryLocalizationHandler = new LocalizationHandler(buildingModuleCategoryLocalizationData);

    const getSlots = () => {
        return new MapStream(slots)
            .sorted((a, b) => buildingModuleCategoryLocalizationHandler.get(a.key).localeCompare(buildingModuleCategoryLocalizationHandler.get(b.key)))
            .map((slot, amount) => (
                <tr key={slot}>
                    <td>{buildingModuleCategoryLocalizationHandler.get(slot)}</td>
                    <td>{amount}</td>
                </tr>
            ))
            .toList();
    }

    return (
        <table className="skyxplore-game-modify-surface-construction-area-slots formatted-table">
            <thead>
                <tr>
                    <th>{localizationHandler.get("slot-name")}</th>
                    <th>{localizationHandler.get("slot-amount")}</th>
                </tr>
            </thead>
            <tbody>
                {getSlots()}
            </tbody>
        </table>
    );
}

export default ConstructionAreaSlots;