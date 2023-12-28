import React from "react";
import "./planet_queue.css"
import localizationData from "./planet_queue_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";

const PlanetQueue = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="skyxplore-game-planet-queue">
            <div id="skyxplore-game-planet-queue-title">{localizationHandler.get("title")}</div>
        </div>
    );
}

export default PlanetQueue;