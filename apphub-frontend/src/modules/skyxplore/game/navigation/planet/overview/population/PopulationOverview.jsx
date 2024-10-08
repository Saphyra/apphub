import React, { useEffect, useState } from "react";
import "./population_overview.css";
import localizationData from "./population_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import NavigationHistoryItem from "../../../NavigationHistoryItem";
import PageName from "../../../PageName";
import Button from "../../../../../../../common/component/input/Button";
import { isTrue } from "../../../../../../../common/js/Utils";

const PopulationOverview = ({ population, capacity, openPage, planetId, tabSettings, updateTabSettings }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [displayDetails, setDisplayDetails] = useState(true);

    useEffect(
        () => {
            setDisplayDetails(isTrue(tabSettings.tabs.population));
        },
        [tabSettings]
    );

    const updateDisplayDetails = (newValue) => {
        tabSettings.tabs.population = newValue;

        updateTabSettings(tabSettings);
        setDisplayDetails(newValue);
    }

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
            <Button
                className="skyxplore-game-planet-overview-tab-expand-button"
                label={displayDetails ? "-" : "+"}
                onclick={() => updateDisplayDetails(!displayDetails)}
            />
            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            {displayDetails &&
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
            }

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