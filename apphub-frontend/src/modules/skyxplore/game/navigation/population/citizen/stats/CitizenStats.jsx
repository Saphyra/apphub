import React from "react";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import CitizenStatusBar from "../status_bar/CitizenStatusBar";
import citizenLocalizationData from "../../../../common/localization/citizen_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";

const CitizenStats = ({ stats, hiddenProperties }) => {
    const citizenLocalizationHandler = new LocalizationHandler(citizenLocalizationData);

    const getContent = () => {
        return new MapStream(stats)
            .filter(stat => hiddenProperties.indexOf(stat) < 0)
            .sorted((a, b) => citizenLocalizationHandler.get(a.key).localeCompare(citizenLocalizationHandler.get(b.key)))
            .map((stat, data) => <CitizenStatusBar
                key={stat}
                type={stat}
                label={getLabel(stat, data.value, data.maxValue)}
                value={data.value}
                max={data.maxValue}
            />)
            .toList();
    }

    const getLabel = (stat, value, maxValue) => {
        const percentage = Math.round(value / maxValue * 100);

        return (
            <span>
                <span className="skyxplore-game-population-citizen-stat-name">{citizenLocalizationHandler.get(stat)}</span>
                <span>: </span>
                <span>
                    <span className="skyxplore-game-population-citizen-stat-value">{percentage}</span>
                    <span>%</span>
                </span>
            </span>
        );
    }

    return (
        <div className="skyxplore-game-population-citizen-stats">
            {getContent()}
        </div>
    );
}

export default CitizenStats;