import React from "react";
import storageTypeLocalizationData from "../../../../common/localization/storage_type_localization.json"
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";

const StorageTableBody = ({ itemData }) => {
    const storageTypeLocalizationHandler = new LocalizationHandler(storageTypeLocalizationData);

    return (
        <tbody>
            <tr>
                <td>{storageTypeLocalizationHandler.get(itemData.stores)}</td>
                <td>{itemData.capacity}</td>
            </tr>
        </tbody>
    );
}

export default StorageTableBody;