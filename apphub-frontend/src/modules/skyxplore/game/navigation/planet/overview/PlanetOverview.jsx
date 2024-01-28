import React from "react";
import "./planet_overview.css";
import StorageOverview from "./storage/StorageOverview";
import PopulationOverview from "./population/PopulationOverview";
import BuildingOverview from "./building/BuildingOverview";
import PriorityOverview from "./priority/PriorityOverview";

const PlanetOverview = ({
    planetId,
    storage,
    population,
    buildings,
    planetSize,
    priorities,
    setPriorities,
    openPage
}) => {
    return (
        <div id="skyxplore-game-planet-overview">
            <StorageOverview
                storage={storage}
                planetId={planetId}
                openPage={openPage}
            />

            <PopulationOverview
                population={population.population}
                capacity={population.capacity}
                planetId={planetId}
                openPage={openPage}
            />

            <BuildingOverview
                buildings={buildings}
                planetSize={planetSize}
            />

            <PriorityOverview
                priorities={priorities}
                setPriorities={setPriorities}
                planetId={planetId}
            />
        </div>
    );
}

export default PlanetOverview;