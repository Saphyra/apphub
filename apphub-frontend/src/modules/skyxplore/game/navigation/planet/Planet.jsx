import React, { useEffect, useState } from "react";
import PlanetHeader from "./header/PlanetHeader";
import Endpoints from "../../../../../common/js/dao/dao";
import "./planet.css";
import PlanetOverview from "./overview/PlanetOverview";
import PlanetSurface from "./surface/PlanetSurface";
import PlanetQueue from "./queue/PlanetQueue";

const Planet = ({ footer, planetId, closePage, openPage, setConfirmationDialogData }) => {
    const [planetName, setPlanetName] = useState("");
    const [storage, setStorage] = useState(null);
    const [population, setPopulation] = useState({});
    const [buildings, setBuildings] = useState({});
    const [surfaces, setSurfaces] = useState([]);
    const [priorities, setPriorities] = useState({});
    const [queue, setQueue] = useState([]);

    useEffect(() => loadPlanet(), []);

    const loadPlanet = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_PLANET_GET_OVERVIEW.createRequest(null, { planetId: planetId })
                .send();

            setPlanetName(response.planetName);
            setStorage(response.storage)
            setPopulation(response.population);
            setBuildings(response.buildings);
            setSurfaces(response.surfaces);
            setPriorities(response.priorities);
            setQueue(response.queue);
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
                    storage={storage}
                    population={population}
                    buildings={buildings}
                    planetSize={surfaces.length}
                    priorities={priorities}
                    setPriorities={setPriorities}
                    planetId={planetId}
                />

                <PlanetSurface
                    surfaces={surfaces}
                    setConfirmationDialogData={setConfirmationDialogData}
                    planetId={planetId}
                    openPage={openPage}
                />

                <PlanetQueue
                    queue={queue}
                />
            </main>

            {footer}
        </div>
    );
}

export default Planet;