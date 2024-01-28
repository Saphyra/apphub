import React, { useEffect, useState } from "react";
import Endpoints from "../../../../../common/js/dao/dao";
import "./solar_system.css";
import SolarSystemConstants from "./SolarSystemConstants";
import Stream from "../../../../../common/js/collection/Stream";
import NavigationHistoryItem from "../NavigationHistoryItem";
import PageName from "../PageName";
import SolarSystemHeader from "./header/SolarSystemHeader";

const SolarSystem = ({ solarSystemId, footer, closePage, openPage }) => {
    const [solarSystemName, setSolarSystemName] = useState("");
    const [radius, setRadius] = useState(0);
    const [planets, setPlanets] = useState([]);
    const center = radius + SolarSystemConstants.OFFSET + SolarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH;

    useEffect(() => loadSolarSystem(), []);

    const loadSolarSystem = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_GET_SOLAR_SYSTEM.createRequest(null, { solarSystemId: solarSystemId })
                .send();

            setSolarSystemName(response.systemName);
            setRadius(response.radius);
            setPlanets(response.planets);
        }
        fetch();
    }

    const getPlanets = () => {
        return new Stream(planets)
            .map(planet => <circle
                key={planet.planetId}
                className="skyxplore-game-solar-system-planet"
                r={SolarSystemConstants.PLANET_RADIUS}
                cx={center + planet.coordinate.x}
                cy={center + planet.coordinate.y}
                onClick={() => openPage(new NavigationHistoryItem(PageName.PLANET, planet.planetId))}
            />)
            .toList();
    }

    const getPlanetNames = () => {
        return new Stream(planets)
            .map(planet => <text
                key={planet.planetId}
                className="skyxplore-game-solar-system-planet-name"
                x={center + planet.coordinate.x}
                y={center + planet.coordinate.y - SolarSystemConstants.PLANET_NAME_OFFSET}
                textAnchor="middle"
                pointerEvents="none"
            >
                {planet.planetName}
            </text>
            )
            .toList();
    }

    const mapSize = center * 2;

    return (
        <div>
            <SolarSystemHeader
                solarSystemId={solarSystemId}
                solarSystemName={solarSystemName}
                closePage={closePage}
                setSolarSystemName={setSolarSystemName}
            />

            <main id="skyxplore-game-solar-system">
                <svg
                    id="skyxplore-game-solar-system-svg"
                    style={{
                        width: mapSize
                    }}
                    viewBox={"0 0 " + mapSize + " " + mapSize}
                >
                    <circle
                        id="skyxplore-game-solar-system-edge"
                        r={radius + SolarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH}
                        cx={radius + SolarSystemConstants.OFFSET + SolarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH}
                        cy={radius + SolarSystemConstants.OFFSET + SolarSystemConstants.SOLAR_SYSTEM_BORDER_WIDTH}
                    />

                    <circle
                        id="skyxplore-game-solar-system-star"
                        r={SolarSystemConstants.STAR_RADIUS}
                        cx={center}
                        cy={center}
                    />

                    {getPlanets()}
                    {getPlanetNames()}
                </svg>
            </main>

            {footer}
        </div>
    );
}

export default SolarSystem;