import React from "react";
import BuildingType from "../../../constants/BuildingType";
import Utils from "../../../../../../../common/js/Utils";
import ProductionTableHead from "./ProductionTableHead";
import StorageTableHead from "./StorageTableHead";

const BuildingEffectTableHead = ({ buildingType, localizationHandler }) => {
    switch (buildingType) {
        case BuildingType.PRODUCTION:
            return <ProductionTableHead
                localizationHandler={localizationHandler}
            />
        case BuildingType.STORAGE:
            return <StorageTableHead
                localizationHandler={localizationHandler}
            />
        default:
            Utils.throwException("IllegalArgument", "Unhandled buildingType " + buildingType);
    }
}

export default BuildingEffectTableHead;