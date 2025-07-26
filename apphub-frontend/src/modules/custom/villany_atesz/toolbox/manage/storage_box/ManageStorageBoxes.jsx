import React, { useState } from "react";
import useLoader from "../../../../../../common/hook/Loader";
import Stream from "../../../../../../common/js/collection/Stream";
import ManagedStorageBox from "./ManagedStorageBox";
import { VILLANY_ATESZ_GET_STORAGE_BOXES } from "../../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const ManageStorageBoxes = ({ setConfirmationDialogData, localizationHandler }) => {
    const [storageBoxes, setStorageBoxes] = useState([]);

    useLoader({ request: VILLANY_ATESZ_GET_STORAGE_BOXES.createRequest(), mapper: setStorageBoxes });

    const getItems = () => {
        return new Stream(storageBoxes)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(storageBox => <ManagedStorageBox
                key={storageBox.storageBoxId}
                setConfirmationDialogData={setConfirmationDialogData}
                localizationHandler={localizationHandler}
                storageBox={storageBox}
                storageBoxes={storageBoxes}
                setStorageBoxes={setStorageBoxes}
            />
            )
            .toList()
    }

    return (
        <div id="villany-atesz-toolbox-manage-storage-boxes" className="villany-atesz-toolbox-manage-tab">
            <h3 className="villany-atesz-toolbox-manage-tab-title">{localizationHandler.get("storage-boxes")}</h3>

            <div className="villany-atesz-toolbox-manage-tab-content">
                {getItems()}
            </div>
        </div>
    );
}

export default ManageStorageBoxes;