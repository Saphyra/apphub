import React, { useEffect, useState } from "react";
import localizationData from "./building_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import Stream from "../../../../../../../common/js/collection/Stream";
import surfaceLocalizationData from "../../../../common/localization/surface_localization.json";
import BuildingTypeOverview from "./type/BuildingTypeOverview";
import Button from "../../../../../../../common/component/input/Button";
import { isTrue } from "../../../../../../../common/js/Utils";

const BuildingOverview = ({ buildings, planetSize, tabSettings, updateTabSettings }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    const [displayDetails, setDisplayDetails] = useState(true);

    useEffect(
        () => {
            setDisplayDetails(isTrue(tabSettings.tabs.buildings));
        },
        [tabSettings]
    );

    const updateDisplayDetails = (newValue) => {
        tabSettings.tabs.buildings = newValue;

        updateTabSettings(tabSettings);
        setDisplayDetails(newValue);
    }

    const getBuildings = () => {
        return new MapStream(buildings)
            .sorted((a, b) => surfaceLocalizationHandler.get(a.key).localeCompare(surfaceLocalizationHandler.get(b.key)))
            .map((surfaceType, surfaceTypeDetails) => <BuildingTypeOverview
                key={surfaceType}
                surfaceType={surfaceType}
                surfaceTypeDetails={surfaceTypeDetails}
                surfaceLocalizationHandler={surfaceLocalizationHandler}
                tabSettings={tabSettings}
                updateTabSettings={updateTabSettings}
            />)
            .toList();
    }

    const getTotalBuildingAmount = () => {
        return new MapStream(buildings)
            .toListStream()
            .map(surfaceTypeDetails => surfaceTypeDetails.usedSlots)
            .sum();
    }

    const getTotalLevel = () => {
        return new MapStream(buildings)
            .toListStream()
            .map(surfaceTypeDetails => surfaceTypeDetails.buildingDetails)
            .flatMap(buildingDetails => new Stream(buildingDetails))
            .map(buildingSummary => buildingSummary.levelSum)
            .sum();
    }

    return (
        <div id="skyxplore-game-planet-overview-building" className="skyxplore-gamep-planet-overview-tab">
            <Button
                className="skyxplore-game-planet-overview-tab-expand-button"
                label={displayDetails ? "-" : "+"}
                onclick={() => updateDisplayDetails(!displayDetails)}
            />

            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            {displayDetails &&
                [
                    getBuildings(),

                    <div
                        key="total"
                        id="skyxplore-game-planet-overview-building-total"
                        className="skyxplore-gamep-planet-overview-tab-item"
                    >
                        <span>{localizationHandler.get("total")}</span>
                        <span>: </span>
                        <span id="skyxplore-game-planet-overview-building-total-buildings">{getTotalBuildingAmount()}</span>
                        <span> / </span>
                        <span id="skyxplore-game-planet-overview-building-planet-size">{planetSize}</span>
                        <span> (</span>
                        <span>{localizationHandler.get("level")}</span>
                        <span>: </span>
                        <span id="skyxplore-game-planet-overview-building-total-level">{getTotalLevel()}</span>
                        <span>)</span>
                    </div>
                ]
            }
        </div>
    );
}

export default BuildingOverview;