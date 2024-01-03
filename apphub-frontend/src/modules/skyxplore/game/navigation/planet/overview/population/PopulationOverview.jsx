import React from "react";
import "./population_overview.css";
import localizationData from "./population_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import NavigationHistoryItem from "../../../NavigationHistoryItem";
import PageName from "../../../PageName";

const PopulationOverview = ({ population, capacity, openPage, planetId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const percentage = population / capacity * 100;

    const getBackgroundColor = () => {
        if (percentage === 100) {
            return "red";
        } else if (percentage >= 90) {
            return "orange";
        } else {
            return "green";
        }
    }

    const width = "calc(" + percentage + "% - 10px)";

    return (
        <div id="skyxplore-game-planet-overview-population" className="skyxplore-gamep-planet-overview-tab">
            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            <div id="skyxplore-game-planet-overview-population-status">
                <div
                    id="skyxplore-game-planet-overview-population-status-bar"
                    style={{
                        width: width,
                        backgroundColor: getBackgroundColor()
                    }}
                />

                <div
                    id="skyxplore-game-planet-overview-population-status-text"
                    className="skyxplore-gamep-planet-overview-tab-item"
                >
                    <span id="skyxplore-game-planet-overview-population-status-current">{population}</span>
                    <span> / </span>
                    <span id="skyxplore-game-planet-overview-population-status-capacity">{capacity}</span>
                </div>
            </div>

            <div
                id="skyxplore-game-planet-overview-population-details-button"
                className="button"
                onClick={() => openPage(new NavigationHistoryItem(PageName.POPULATION, planetId))}
            >
                {localizationHandler.get("population-details")}
            </div>
        </div>
    );
}

export default PopulationOverview;