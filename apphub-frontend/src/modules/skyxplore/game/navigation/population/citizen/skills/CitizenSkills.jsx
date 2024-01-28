import React from "react";
import CitizenStatusBar from "../status_bar/CitizenStatusBar";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import citizenLocalizationData from "../../../../common/localization/citizen_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import localizationData from "./citizen_skill_localization.json";

const CitizenSkills = ({ skills, hiddenProperties }) => {
    const citizenLocalizationHandler = new LocalizationHandler(citizenLocalizationData);
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        return new MapStream(skills)
            .filter(skill => hiddenProperties.indexOf(skill) < 0)
            .sorted((a, b) => citizenLocalizationHandler.get(a.key).localeCompare(citizenLocalizationHandler.get(b.key)))
            .map((skill, data) => <CitizenStatusBar
                key={skill}
                type={skill}
                label={getLabel(skill, data)}
                value={data.experience}
                max={data.nextLevel}
            />)
            .toList();
    }

    const getLabel = (skill, data) => {
        return (
            <span>
                <span className="skyxplore-game-population-citizen-skill-name">{citizenLocalizationHandler.get(skill)}</span>
                <span> - </span>
                <span>{localizationHandler.get("level")}</span>
                <span> </span>
                <span>{data.level}</span>
                <span>{" ("}</span>
                <span>{Math.round(data.experience / data.nextLevel * 100)}</span>
                <span>{"%)"}</span>
            </span>
        );
    }

    return (
        <div className="skyxplore-game-population-citizen-skills">
            {getContent()}
        </div>
    );
}

export default CitizenSkills;