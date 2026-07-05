import CitizenAssignment from "./assignment/CitizenAssignment";
import "./citizen.css";
import CitizenName from "./name/CitizenName";
import CitizenSkills from "./skills/CitizenSkills";
import CitizenStats from "./stats/CitizenStats";

const Citizen = ({ citizen, hiddenProperties, setDisplaySpinner }) => {
    return (
        <div className="skyxplore-game-population-citizen">
            <CitizenName
                name={citizen.name}
                citizenId={citizen.citizenId}
                setDisplaySpinner={setDisplaySpinner}
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