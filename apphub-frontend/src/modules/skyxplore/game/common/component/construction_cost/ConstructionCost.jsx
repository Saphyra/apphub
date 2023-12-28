import React from "react";
import localizationData from "./construction_cost_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../common/js/collection/MapStream";
import resourceLocalizationData from "../../localization/resource_localization.json";

const ConstructionCost = ({ id, itemData, targetLevel }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

    const constructionRequirements = itemData.constructionRequirements[targetLevel];

    const getResourceRequirements = () => {
        return new MapStream(constructionRequirements.requiredResources)
            .map((dataId, amount) => (
                <tr key={dataId}>
                    <td>{resourceLocalizationHandler.get(dataId)}</td>
                    <td>{amount}</td>
                </tr>
            ))
            .toList();
    }

    return (
        <table id={id} className="formatted-table">
            <thead>
                <tr>
                    <td colSpan={2}>{localizationHandler.get("construction-cost")}</td>
                </tr>
            </thead>
            <tbody>
                {getResourceRequirements()}
                <tr>
                    <td>{localizationHandler.get("required-work-points")}</td>
                    <td>{constructionRequirements.requiredWorkPoints}</td>
                </tr>
            </tbody>
        </table>
    );
}

export default ConstructionCost;