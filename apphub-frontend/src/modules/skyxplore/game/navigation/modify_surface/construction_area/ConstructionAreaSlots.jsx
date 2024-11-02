import React from "react";
import localizationData from "./construction_area_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../common/js/collection/MapStream";
import slotLocalization from "../../../common/localization/construction_area_slot_localization.json";

const ConstructionAreaSlots = ({ slots }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const slotLocalizationHandler = new LocalizationHandler(slotLocalization);

    const getSlots = () => {
        return new MapStream(slots)
            .sorted((a, b) => slotLocalizationHandler.get(a.key).localeCompare(slotLocalizationHandler.get(b.key)))
            .map((slot, amount) => (
                <tr key={slot}>
                    <td>{slotLocalizationHandler.get(slot)}</td>
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