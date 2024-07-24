import React, { useEffect, useState } from "react";
import localizationData from "./storage_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import "./storage_overview.css";
import StorageTypeOverview from "./type/StorageTypeOverview";
import StorageType from "./StorageType";
import Utils from "../../../../../../../common/js/Utils";
import NavigationHistoryItem from "../../../NavigationHistoryItem";
import PageName from "../../../PageName";
import Button from "../../../../../../../common/component/input/Button";

const StorageOverview = ({ storage, openPage, planetId, tabSettings, updateTabSettings }) => {
    const [displayDetails, setDisplayDetails] = useState(true);

    useEffect(
        () => {
            setDisplayDetails(Utils.isTrue(tabSettings.tabs.storageOverview));
        },
        [tabSettings]
    );

    const updateDisplayDetails = (newValue) => {
        tabSettings.tabs.storageOverview = newValue;

        updateTabSettings(tabSettings);
        setDisplayDetails(newValue);
    }

    if (!Utils.hasValue(storage)) {
        return null;
    }

    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="skyxplore-game-planet-overview-storage" className="skyxplore-gamep-planet-overview-tab">
            <Button
                className="skyxplore-game-planet-overview-tab-expand-button"
                label={displayDetails ? "-" : "+"}
                onclick={() => updateDisplayDetails(!displayDetails)}
            />

            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            {displayDetails &&
                [
                    <StorageTypeOverview
                        key="energy"
                        storageType={StorageType.ENERGY}
                        data={storage[StorageType.ENERGY]}
                        localizationHandler={localizationHandler}
                        tabSettings={tabSettings}
                        updateTabSettings={updateTabSettings}
                    />,

                    <StorageTypeOverview
                        key="liquid"
                        storageType={StorageType.LIQUID}
                        data={storage[StorageType.LIQUID]}
                        localizationHandler={localizationHandler}
                        tabSettings={tabSettings}
                        updateTabSettings={updateTabSettings}
                    />,

                    <StorageTypeOverview
                        key="bulk"
                        storageType={StorageType.BULK}
                        data={storage[StorageType.BULK]}
                        localizationHandler={localizationHandler}
                        tabSettings={tabSettings}
                        updateTabSettings={updateTabSettings}
                    />
                ]
            }

            <div
                id="skyxplore-game-planet-overview-open-storage-button"
                className="button"
                onClick={() => openPage(new NavigationHistoryItem(PageName.STORAGE, planetId))}
            >
                {localizationHandler.get("open-storage")}
            </div>
        </div>
    );
}

export default StorageOverview;