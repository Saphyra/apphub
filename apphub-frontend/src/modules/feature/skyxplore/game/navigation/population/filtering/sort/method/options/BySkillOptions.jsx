import React from "react";
import getSelector from "./OptionSelectorFactory";

const BySkillOptions = ({ selectedSkill, skills, setSelectedSkill }) => {
    return getSelector(
        "skyxplore-game-population-skill-selector",
        selectedSkill,
        skills,
        setSelectedSkill
    );
}

export default BySkillOptions;