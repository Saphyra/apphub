import React from "react";
import localizationData from "./building_effect_table_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import BuildingType from "../../constants/BuildingType";
import Utils from "../../../../../../common/js/Utils";
import MapStream from "../../../../../../common/js/collection/MapStream";
import resourceLocalizationData from "../../localization/resource_localization.json";

//TODO split
const BuildingEffectTable = ({ id, className, surfaceType, itemData, currentLevel }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

    const getTableHead = () => {
        switch (itemData.buildingType) {
            case BuildingType.PRODUCTION:
                return (
                    <thead>
                        <tr>
                            <th colSpan={2}>{localizationHandler.get("produces")}</th>
                        </tr>
                        <tr>
                            <th>{localizationHandler.get("commodity")}</th>
                            <th>{localizationHandler.get("components")}</th>
                        </tr>
                    </thead>
                )
            default:
                Utils.throwException("IllegalArgument", "Unhandled buildingType " + itemData.buildingType);
        }
    }

    const getTableBody = () => {
        switch (itemData.buildingType) {
            case BuildingType.PRODUCTION:
                const rows = new MapStream(itemData.gives)
                    .filter((dataId, production) => production.placed.indexOf(surfaceType) > -1)
                    .map((dataId, production) => {
                        const resourceRequirements = new MapStream(production.constructionRequirements.requiredResources)
                            .map((resourceDataId, amount) => (
                                <div key={resourceDataId}>
                                    <span>{resourceLocalizationHandler.get(resourceDataId)}</span>
                                    <span>: </span>
                                    <span>{amount}</span>
                                </div>
                            ))
                            .toList();

                        return (
                            <tr key={dataId}>
                                <td>
                                    <span>{production.amount}</span>
                                    <span> x </span>
                                    <span>{resourceLocalizationHandler.get(dataId)}</span>
                                </td>
                                <td>
                                    {resourceRequirements}
                                    <div>
                                        <span>{localizationHandler.get("required-work-points")}</span>
                                        <span>: </span>
                                        <span>{production.constructionRequirements.requiredWorkPoints}</span>
                                    </div>
                                </td>
                            </tr>
                        )
                    })
                    .toList();
                return (
                    <tbody>
                        {rows}

                        <tr>
                            <td colSpan={2} className="centered">
                                <span>{localizationHandler.get("workplaces")}</span>
                                <span>: </span>
                                <span>{(currentLevel + 1) * itemData.workers}</span>
                            </td>
                        </tr>
                    </tbody>
                );
            default:
                Utils.throwException("IllegalArgument", "Unhandled buildingType " + itemData.buildingType);
        }
    }

    return (
        <table id={id} className={className + " formatted-table"}>
            {getTableHead()}
            {getTableBody()}
        </table>
    );
}

export default BuildingEffectTable;