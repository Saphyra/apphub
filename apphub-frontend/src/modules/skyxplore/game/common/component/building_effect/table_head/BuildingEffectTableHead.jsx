import React from "react";
import BuildingType from "../../../constants/BuildingType";
import ProductionTableHead from "./ProductionTableHead";
import StorageTableHead from "./StorageTableHead";
import MiscellaneousBuildingEffectTableHead from "./miscellaneous/MiscellaneousBuildingEffectTableHead";
import { throwException } from "../../../../../../../common/js/Utils";

const BuildingEffectTableHead = ({ dataId, buildingType, localizationHandler }) => {
    switch (buildingType) {
        case BuildingType.PRODUCTION:
            return <ProductionTableHead
                localizationHandler={localizationHandler}
            />
        case BuildingType.STORAGE:
            return <StorageTableHead
                localizationHandler={localizationHandler}
            />
        case BuildingType.MISCELLANEOUS:
            return <MiscellaneousBuildingEffectTableHead
                localizationHandler={localizationHandler}
                dataId={dataId}
            />
            break;
        default:
            throwException("IllegalArgument", "Unhandled buildingType " + buildingType);
    }
}

export default BuildingEffectTableHead;