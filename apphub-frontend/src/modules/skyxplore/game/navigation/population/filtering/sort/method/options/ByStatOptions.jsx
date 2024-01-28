import React from "react";
import getSelector from "./OptionSelectorFactory";

const ByStatOptions = ({ selectedSkill, skills, setSelectedSkill }) => {
    return getSelector(
        "skyxplore-game-population-skill-selector",
        selectedSkill,
        skills,
        setSelectedSkill
    );
}

export default ByStatOptions;