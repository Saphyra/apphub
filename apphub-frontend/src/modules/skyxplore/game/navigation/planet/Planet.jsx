import React, { useEffect, useState } from "react";
import PlanetHeader from "./header/PlanetHeader";
import Endpoints from "../../../../../common/js/dao/dao";
import "./planet.css";
import PlanetOverview from "./overview/PlanetOverview";
import PlanetSurface from "./surface/PlanetSurface";
import PlanetQueue from "./queue/PlanetQueue";

const Planet = ({ footer, planetId, closePage, openPage }) => {
    const [planetName, setPlanetName] = useState("");

    useEffect(() => loadPlanet(), []);

    const loadPlanet = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_PLANET_GET_OVERVIEW.createRequest(null, { planetId: planetId })
                .send();

            setPlanetName(response.planetName);
        }
        fetch();
    }

    return (
        <div>
            <PlanetHeader
                planetId={planetId}
                planetName={planetName}
                setPlanetName={setPlanetName}
                closePage={closePage}
            />

            <main id="skyxplore-game-planet">
                <PlanetOverview

                />

                <PlanetSurface

                />

                <PlanetQueue

                />
            </main>

            {footer}
        </div>
    );
}

export default Planet;