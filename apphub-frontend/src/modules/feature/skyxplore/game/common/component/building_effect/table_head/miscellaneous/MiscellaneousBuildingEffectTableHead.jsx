import React from "react";
import DataId from "../../../../constants/DataId";
import ProductionTableHead from "../ProductionTableHead";
import { throwException } from "../../../../../../../../common/js/Utils";

const MiscellaneousBuildingEffectTableHead = ({ dataId, localizationHandler }) => {
    switch (dataId) {
        case DataId.HEADQUARTERS:
            return <ProductionTableHead
                localizationHandler={localizationHandler}
            />
        default:
            throwException("IllegalArgument", "Unhandles MiscellaneousBuilding: " + dataId);
    }
}

export default MiscellaneousBuildingEffectTableHead;