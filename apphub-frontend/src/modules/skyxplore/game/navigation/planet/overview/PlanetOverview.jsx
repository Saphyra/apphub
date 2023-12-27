import React from "react";
import "./planet_overview.css";
import StorageOverview from "./storage/StorageOverview";
import PopulationOverview from "./population/PopulationOverview";

const PlanetOverview = ({ storage, population }) => {
    return (
        <div id="skyxplore-game-planet-overview">
            <StorageOverview
                storage={storage}
            />

            <PopulationOverview
                population={population.population}
                capacity={population.capacity}
            />
        </div>
    );
}

export default PlanetOverview;