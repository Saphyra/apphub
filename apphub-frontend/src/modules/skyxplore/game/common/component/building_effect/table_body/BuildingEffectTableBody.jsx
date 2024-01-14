import React from "react";
import BuildingType from "../../../constants/BuildingType";
import Utils from "../../../../../../../common/js/Utils";
import ProductionTableBody from "./ProductionTableBody";
import StorageTableBody from "./StorageTableBody";

const BuildingEffectTableBody = ({ itemData, surfaceType, localizationHandler, currentLevel }) => {
    switch (itemData.buildingType) {
        case BuildingType.PRODUCTION:
            return <ProductionTableBody
                itemData={itemData}
                surfaceType={surfaceType}
                localizationHandler={localizationHandler}
                currentLevel={currentLevel}
            />
        case BuildingType.STORAGE:
            return <StorageTableBody
                itemData={itemData}
            />
        default:
            Utils.throwException("IllegalArgument", "Unhandled buildingType " + itemData.buildingType);
    }
}

export default BuildingEffectTableBody;