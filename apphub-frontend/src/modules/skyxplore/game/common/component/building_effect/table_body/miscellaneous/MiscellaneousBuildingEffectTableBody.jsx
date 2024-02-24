import React from "react";
import DataId from "../../../../constants/DataId";
import Utils from "../../../../../../../../common/js/Utils";
import ProductionTableBody from "../ProductionTableBody";

const MiscellaneousBuildingEffectTableBody = ({ itemData, localizationHandler, surfaceType, currentLevel }) => {
    switch (itemData.id) {
        case DataId.HEADQUARTERS:
            return <ProductionTableBody
                gives={itemData.data.gives}
                workers={itemData.data.workers}
                surfaceType={surfaceType}
                localizationHandler={localizationHandler}
                currentLevel={currentLevel}
            />
        default:
            Utils.throwException("IllegalArgument", "Unhandled miscellaneous building: " + itemData.id);
    }
}

export default MiscellaneousBuildingEffectTableBody;