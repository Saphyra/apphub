import React, { useEffect, useState } from "react";
import Button from "../../../../../../../../common/component/input/Button";
import BuildingTypeOverviewDetails from "./details/BuildingTypeOverviewDetails";
import Utils from "../../../../../../../../common/js/Utils";

const BuildingTypeOverview = ({ surfaceType, surfaceTypeDetails, surfaceLocalizationHandler, tabSettings, updateTabSettings }) => {
    const [displayDetails, setDisplayDetails] = useState(false);

    useEffect(
        () => {
            setDisplayDetails(Utils.isTrue(tabSettings.building[surfaceType]));
        },
        [tabSettings]
    );

    const updateDisplayDetails = (newValue) => {
        tabSettings.building[surfaceType] = newValue;

        updateTabSettings(tabSettings);
        setDisplayDetails(newValue);
    }


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
                    onclick={() => updateDisplayDetails(!displayDetails)}
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