import React, { useState } from "react";
import Button from "../../../../../../../../common/component/input/Button";
import StatusBar from "./status_bar/StatusBar";
import StorageOverviewDetails from "./details/StorageOverviewDetails";

const StorageTypeOverview = ({ storageType, localizationHandler, data }) => {
    const [displayDetails, setDisplayDetails] = useState(false);

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
                            onclick={() => setDisplayDetails(!displayDetails)}
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