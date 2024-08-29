import React, { useState } from "react";
import useLoader from "../../../../../../common/hook/Loader";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import ManagedStorageBox from "./ManagedStorageBox";

const ManageStorageBoxes = ({ setConfirmationDialogData, localizationHandler }) => {
    const [storageBoxes, setStorageBoxes] = useState([]);

    useLoader(Endpoints.VILLANY_ATESZ_GET_STORAGE_BOXES.createRequest(), setStorageBoxes);

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