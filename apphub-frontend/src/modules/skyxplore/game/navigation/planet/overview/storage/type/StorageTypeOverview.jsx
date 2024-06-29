import React, { useEffect, useState } from "react";
import Button from "../../../../../../../../common/component/input/Button";
import StatusBar from "./status_bar/StatusBar";
import StorageOverviewDetails from "./details/StorageOverviewDetails";
import Utils from "../../../../../../../../common/js/Utils";

const StorageTypeOverview = ({ storageType, localizationHandler, data, tabSettings, updateTabSettings }) => {
    const [displayDetails, setDisplayDetails] = useState(false);

    useEffect(
        () => {
            setDisplayDetails(Utils.isTrue(tabSettings.storage[storageType]));
        },
        [tabSettings]
    );

    const updateDisplayDetails = (newValue) => {
        tabSettings.storage[storageType] = newValue;

        updateTabSettings(tabSettings);
        setDisplayDetails(newValue);
    }

    return (
        <div
            id={"skyxplore-game-planet-overview-storage-" + storageType}
            className="skyxplore-gamep-planet-overview-tab-item"
        >
            <div className="skyxplore-game-planet-overview-storage-type">
                <StatusBar
                    capacity={data.capacity}
                    actualAmount={data.actualResourceAmount}
                    allocatedAmount={data.allocatedResourceAmount}
                    reservedAmount={data.reservedStorageAmount}
                />

                <div className="skyxplore-game-planet-overview-storage-label">
                    <span>{localizationHandler.get(storageType)}</span>
                    <span>: </span>
                    <span className="skyxplore-game-planet-overview-storage-label-actual">{data.actualResourceAmount}</span>
                    <span> (</span>
                    <span className="skyxplore-game-planet-overview-storage-label-allocated">{data.allocatedResourceAmount}</span>
                    <span>) / </span>
                    <span>{data.capacity}</span>
                    <span> - {localizationHandler.get("reserved")}: </span>
                    <span className="skyxplore-game-planet-overview-storage-label-reserved">{data.reservedStorageAmount}</span>

                    {data.resourceDetails.length > 0 &&
                        <Button
                            className="skyxplore-game-planet-overview-tab-item-expand-button"
                            label={displayDetails ? "-" : "+"}
                            onclick={() => updateDisplayDetails(!displayDetails)}
                        />
                    }
                </div>
            </div>

            {displayDetails &&
                <StorageOverviewDetails
                    resources={data.resourceDetails}
                    localizationHandler={localizationHandler}
                />
            }
        </div>
    );
}

export default StorageTypeOverview;