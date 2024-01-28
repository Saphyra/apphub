import React from "react";
import localizationData from "./building_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import Stream from "../../../../../../../common/js/collection/Stream";
import surfaceLocalizationData from "../../../../common/localization/surface_localization.json";
import BuildingTypeOverview from "./type/BuildingTypeOverview";

const BuildingOverview = ({ buildings, planetSize }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);

    const getBuildings = () => {
        return new MapStream(buildings)
            .sorted((a, b) => surfaceLocalizationHandler.get(a.key).localeCompare(surfaceLocalizationHandler.get(b.key)))
            .map((surfaceType, surfaceTypeDetails) => <BuildingTypeOverview
                key={surfaceType}
                surfaceType={surfaceType}
                surfaceTypeDetails={surfaceTypeDetails}
                surfaceLocalizationHandler={surfaceLocalizationHandler}
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
            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            {getBuildings()}

            <div id="skyxplore-game-planet-overview-building-total" className="skyxplore-gamep-planet-overview-tab-item">
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
        </div>
    );
}

export default BuildingOverview;