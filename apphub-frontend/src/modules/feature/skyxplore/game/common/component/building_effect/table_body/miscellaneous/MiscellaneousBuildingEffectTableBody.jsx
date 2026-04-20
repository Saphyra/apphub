import React from "react";
import DataId from "../../../../constants/DataId";
import ProductionTableBody from "../ProductionTableBody";
import { throwException } from "../../../../../../../../common/js/Utils";

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
            throwException("IllegalArgument", "Unhandled miscellaneous building: " + itemData.id);
    }
}

export default MiscellaneousBuildingEffectTableBody;