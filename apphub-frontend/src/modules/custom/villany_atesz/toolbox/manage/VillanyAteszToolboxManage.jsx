import React from "react";
import localizationData from "./villany_atesz_toolbox_manage_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_toolbox_manage.css";
import ManageToolTypes from "./tool_type/ManageToolTypes";
import ManageStorageBoxes from "./storage_box/ManageStorageBoxes";

const VillanyAteszToolboxManage = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="villany-atesz-toolbox-manage">
            <ManageToolTypes
                setConfirmationDialogData={setConfirmationDialogData}
                localizationHandler={localizationHandler}
            />

            <ManageStorageBoxes
                setConfirmationDialogData={setConfirmationDialogData}
                localizationHandler={localizationHandler}
            />
        </div>
    );
}

export default VillanyAteszToolboxManage;