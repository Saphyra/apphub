import React from "react";
import DataId from "../../../../constants/DataId";
import Utils from "../../../../../../../../common/js/Utils";
import ProductionTableHead from "../ProductionTableHead";

const MiscellaneousBuildingEffectTableHead = ({ dataId, localizationHandler }) => {
    switch (dataId) {
        case DataId.HEADQUARTERS:
            return <ProductionTableHead
                localizationHandler={localizationHandler}
            />
        default:
            Utils.throwException("IllegalArgument", "Unhandles MiscellaneousBuilding: " + dataId);
    }
}

export default MiscellaneousBuildingEffectTableHead;