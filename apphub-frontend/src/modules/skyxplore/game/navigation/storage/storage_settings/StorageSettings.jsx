import React, { useState } from "react";
import useLoader from "../../../../../../common/hook/Loader";
import "./storage_settings.css";
import StorageSettingCreator from "./storage_setting_creator/StorageSettingCreator";
import localizationData from "./storage_settings_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../common/js/collection/Stream";
import resosurceLocalizationData from "../../../common/localization/resource_localization.json";
import StorageSetting from "./storage_setting/StorageSetting";
import { SKYXPLORE_PLANET_GET_STORAGE_SETTINGS } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const StorageSettings = ({ planetId, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resosurceLocalizationData);

    const [storageSettings, setStorageSettings] = useState([]);

    useLoader(SKYXPLORE_PLANET_GET_STORAGE_SETTINGS.createRequest(null, { planetId: planetId }), setStorageSettings);

    const getStorageSettings = () => {
        return new Stream(storageSettings)
            .sorted((a, b) => resourceLocalizationHandler.get(a.dataId).localeCompare(resourceLocalizationHandler.get(b.dataId)))
            .map(storageSetting => <StorageSetting
                key={storageSetting.storageSettingId}
                storageSetting={storageSetting}
                setStorageSettings={setStorageSettings}
                setConfirmationDialogData={setConfirmationDialogData}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-storage-settings">
            <div id="skyxplore-game-storage-settings-title">{localizationHandler.get("title")}</div>

            <div id="skyxplore-game-storage-settings-content">
                <StorageSettingCreator
                planetId={planetId}
                    storageSettings={storageSettings}
                    setStorageSettings={setStorageSettings}
                />

                <div id="skyxplore-game-storage-setting-list">
                    {getStorageSettings()}
                </div>
            </div>
        </div>
    )
}

export default StorageSettings;