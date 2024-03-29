import React from "react";
import "./citizen.css";
import CitizenName from "./name/CitizenName";
import CitizenStats from "./stats/CitizenStats";
import CitizenSkills from "./skills/CitizenSkills";
import CitizenAssignment from "./assignment/CitizenAssignment";

const Citizen = ({ citizen, hiddenProperties }) => {
    return (
        <div className="skyxplore-game-population-citizen">
            <CitizenName
                name={citizen.name}
                citizenId={citizen.citizenId}
            />

            <div className="skyxplore-game-population-citizen-content">
                <CitizenAssignment
                    assignment={citizen.assignment}
                />

                <CitizenStats
                    stats={citizen.stats}
                    hiddenProperties={hiddenProperties}
                />

                <CitizenSkills
                    skills={citizen.skills}
                    hiddenProperties={hiddenProperties}
                />
            </div>
        </div>
    );
}

export default Citizen;