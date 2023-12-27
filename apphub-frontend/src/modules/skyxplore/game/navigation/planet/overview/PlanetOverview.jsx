import React from "react";
import "./planet_overview.css";
import StorageOverview from "./storage/StorageOverview";

const PlanetOverview = ({ storage }) => {
    return (
        <div id="skyxplore-game-planet-overview">
            <StorageOverview
                storage={storage}
            />
        </div>
    );
}

export default PlanetOverview;