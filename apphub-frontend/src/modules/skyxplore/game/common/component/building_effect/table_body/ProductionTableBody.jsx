import React from "react";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import resourceLocalizationData from "../../../../common/localization/resource_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";

const ProductionTableBody = ({ itemData, surfaceType, localizationHandler, currentLevel }) => {
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

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
}

export default ProductionTableBody;