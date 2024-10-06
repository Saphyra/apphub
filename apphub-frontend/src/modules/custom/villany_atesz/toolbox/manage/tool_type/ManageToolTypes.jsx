import React, { useState } from "react";
import useLoader from "../../../../../../common/hook/Loader";
import Stream from "../../../../../../common/js/collection/Stream";
import ManagedToolType from "./ManagedToolType";
import { VILLANY_ATESZ_GET_TOOL_TYPES } from "../../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const ManageToolTypes = ({ setConfirmationDialogData, localizationHandler }) => {
    const [toolTypes, setToolTypes] = useState([]);

    useLoader(VILLANY_ATESZ_GET_TOOL_TYPES.createRequest(), setToolTypes);

    const getItems = () => {
        return new Stream(toolTypes)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(toolType => <ManagedToolType
                key={toolType.toolTypeId}
                setConfirmationDialogData={setConfirmationDialogData}
                localizationHandler={localizationHandler}
                toolType={toolType}
                toolTypes={toolTypes}
                setToolTypes={setToolTypes}
            />
            )
            .toList();
    }

    return (
        <div id="villany-atesz-toolbox-manage-tool-types" className="villany-atesz-toolbox-manage-tab">
            <h3 className="villany-atesz-toolbox-manage-tab-title">{localizationHandler.get("tool-types")}</h3>

            <div className="villany-atesz-toolbox-manage-tab-content">
                {getItems()}
            </div>
        </div>
    );
}

export default ManageToolTypes;