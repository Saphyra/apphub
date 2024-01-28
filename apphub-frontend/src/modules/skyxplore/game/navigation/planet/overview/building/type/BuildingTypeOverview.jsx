import React, { useState } from "react";
import Button from "../../../../../../../../common/component/input/Button";
import BuildingTypeOverviewDetails from "./details/BuildingTypeOverviewDetails";

const BuildingTypeOverview = ({ surfaceType, surfaceTypeDetails, surfaceLocalizationHandler }) => {
    const [displayDetails, setDisplayDetails] = useState(false);

    return (
        <div
            id={"skyxplore-game-planet-overview-building-" + surfaceType.toLowerCase()}
            className="skyxplore-gamep-planet-overview-tab-item"
        >
            <span>{surfaceLocalizationHandler.get(surfaceType)}</span>
            <span>: </span>
            <span className="skyxplore-game-planet-overview-building-used-slots">{surfaceTypeDetails.usedSlots}</span>
            <span> / </span>
            <span className="skyxplore-game-planet-overview-building-total-slots">{surfaceTypeDetails.slots}</span>

            {surfaceTypeDetails.usedSlots > 0 &&
                <Button
                    className="skyxplore-game-planet-overview-tab-item-expand-button"
                    label={displayDetails ? "-" : "+"}
                    onclick={() => setDisplayDetails(!displayDetails)}
                />
            }

            {displayDetails &&
                <BuildingTypeOverviewDetails
                    buildingDetails={surfaceTypeDetails.buildingDetails}
                />
            }
        </div>
    );
}

export default BuildingTypeOverview;