import React from "react";
import "./planet_overview.css";
import StorageOverview from "./storage/StorageOverview";
import PopulationOverview from "./population/PopulationOverview";
import BuildingOverview from "./building/BuildingOverview";

const PlanetOverview = ({
    storage,
    population,
    buildings,
    planetSize
}) => {
    return (
        <div id="skyxplore-game-planet-overview">
            <StorageOverview
                storage={storage}
            />

            <PopulationOverview
                population={population.population}
                capacity={population.capacity}
            />

            <BuildingOverview
                buildings={buildings}
                planetSize={planetSize}
            />
        </div>
    );
}

export default PlanetOverview;