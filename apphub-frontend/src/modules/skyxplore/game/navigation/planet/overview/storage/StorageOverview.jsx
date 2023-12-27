import React from "react";
import localizationData from "./storage_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import "./storage_overview.css";
import StorageTypeOverview from "./type/StorageTypeOverview";
import StorageType from "./StorageType";
import Utils from "../../../../../../../common/js/Utils";

const StorageOverview = ({ storage }) => {
    if (!Utils.hasValue(storage)) {
        return null;
    }

    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="skyxplore-game-planet-overview-storage" className="skyxplore-gamep-planet-overview-tab">
            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            <StorageTypeOverview
                storageType={StorageType.ENERGY}
                data={storage[StorageType.ENERGY]}
                localizationHandler={localizationHandler}
            />

            <StorageTypeOverview
                storageType={StorageType.LIQUID}
                data={storage[StorageType.LIQUID]}
                localizationHandler={localizationHandler}
            />

            <StorageTypeOverview
                storageType={StorageType.BULK}
                data={storage[StorageType.BULK]}
                localizationHandler={localizationHandler}
            />

            <div
                id="skyxplore-game-planet-overview-storage-settings"
                className="button"
            //TODO add onclick
            >
                {localizationHandler.get("storage-settings")}
            </div>
        </div>
    );
}

export default StorageOverview;