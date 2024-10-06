import React, { useEffect, useState } from "react";
import "./map.css";
import MapConstants from "./MapConstants";
import Stream from "../../../../../common/js/collection/Stream";
import NavigationHistoryItem from "../NavigationHistoryItem";
import PageName from "../PageName";
import { SKYXPLORE_GAME_MAP } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const Map = ({ openPage, footer }) => {
    const [universeSize, setUniverseSize] = useState(0);
    const [solarSystems, setSolarSystems] = useState([]);

    useEffect(() => loadUniverse(), []);

    const loadUniverse = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_GAME_MAP.createRequest()
                .send();

            setUniverseSize(response.universeSize);
            setSolarSystems(response.solarSystems);
        }
        fetch();
    }

    const getSolarSystems = () => {
        return new Stream(solarSystems)
            .map(solarSystem => <SolarSystem
                key={solarSystem.solarSystemId}
                solarSystem={solarSystem}
                openPage={openPage}
            />)
            .toList();
    }

    const getSolarSystemNames = () => {
        return new Stream(solarSystems)
            .map(solarSystem => <SolarSystemName
                key={solarSystem.solarSystemId}
                solarSystem={solarSystem}
            />)
            .toList();
    }

    const mapWidth = universeSize + MapConstants.X_OFFSET * 2;
    const mapHeight = universeSize + MapConstants.Y_OFFSET * 2;

    return (
        <div>
            <main id="skyxplore-game-map" className="headless">
                <div id="skyxplore-game-map-container">
                    <svg
                        id="skyxplore-game-map-svg"
                        style={{
                            width: mapWidth
                        }}
                        viewBox={"0 0 " + mapWidth + " " + mapHeight}
                    >
                        {getSolarSystems()}
                        {getSolarSystemNames()}
                    </svg>
                </div>
            </main>

            {footer}
        </div>
    );
}

const SolarSystem = ({ solarSystem, openPage }) => {
    return <circle
        className="skyxplore-game-map-solar-system"
        onClick={() => openPage(new NavigationHistoryItem(PageName.SOLAR_SYSTEM, solarSystem.solarSystemId))}
        r={MapConstants.STAR_SIZE}
        cx={solarSystem.coordinate.x + MapConstants.X_OFFSET}
        cy={solarSystem.coordinate.y + MapConstants.Y_OFFSET}
    />
}

const SolarSystemName = ({ solarSystem }) => {
    return <text
        className="skyxplore-game-map-solar-system-name"
        x={solarSystem.coordinate.x + MapConstants.X_OFFSET}
        y={solarSystem.coordinate.y - MapConstants.STAR_NAME_OFFSET + MapConstants.Y_OFFSET}
        textAnchor="middle"
        pointerEvents="none"
    >
        {solarSystem.solarSystemName + " (" + solarSystem.planetNum + ")"}
    </text>
}

export default Map;