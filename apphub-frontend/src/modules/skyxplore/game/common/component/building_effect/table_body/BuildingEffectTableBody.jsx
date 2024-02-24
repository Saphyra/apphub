import React from "react";
import BuildingType from "../../../constants/BuildingType";
import Utils from "../../../../../../../common/js/Utils";
import ProductionTableBody from "./ProductionTableBody";
import StorageTableBody from "./StorageTableBody";
import MiscellaneousBuildingEffectTableBody from "./miscellaneous/MiscellaneousBuildingEffectTableBody";

const BuildingEffectTableBody = ({ itemData, surfaceType, localizationHandler, currentLevel }) => {
    switch (itemData.buildingType) {
        case BuildingType.PRODUCTION:
            return <ProductionTableBody
                gives={itemData.gives}
                workers={itemData.workers}
                surfaceType={surfaceType}
                localizationHandler={localizationHandler}
                currentLevel={currentLevel}
            />
        case BuildingType.STORAGE:
            return <StorageTableBody
                itemData={itemData}
            />
        case BuildingType.MISCELLANEOUS:
            return <MiscellaneousBuildingEffectTableBody
                localizationHandler={localizationHandler}
                itemData={itemData}
                surfaceType={surfaceType}
                currentLevel={currentLevel}
            />
            break;
        default:
            Utils.throwException("IllegalArgument", "Unhandled buildingType " + itemData.buildingType);
    }
}

export default BuildingEffectTableBody;