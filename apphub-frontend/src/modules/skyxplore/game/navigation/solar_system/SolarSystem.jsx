import React, { useEffect, useState } from "react";
import Endpoints from "../../../../../common/js/dao/dao";
import "./solar_system.css";
import Button from "../../../../../common/component/input/Button";
import InputField from "../../../../../common/component/input/InputField";
import localizationData from "./solar_system_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Utils from "../../../../../common/js/Utils";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import GameConstants from "../../GameConstants";
import SolarSystemConstants from "./SolarSystemConstants";
import Stream from "../../../../../common/js/collection/Stream";
import NavigationHistoryItem from "../NavigationHistoryItem";
import PageName from "../PageName";

const SolarSystem = ({ solarSystemId, footer, closePage, openPage }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

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
                localizationHandler={localizationHandler}
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

//TODO extract
const SolarSystemHeader = ({ solarSystemId, solarSystemName, closePage, setSolarSystemName, localizationHandler }) => {
    const [enableEditing, setEnableEditing] = useState(false);
    const [modifiedName, setModifiedName] = useState("");

    useEffect(() => setModifiedName(solarSystemName), [solarSystemName]);

    const changeName = async () => {
        if (Utils.isBlank(modifiedName)) {
            NotificationService.showError(localizationHandler.get("solar-system-name-blank"));
            return;
        }

        if (modifiedName.length > GameConstants.SOLAR_SYSTEM_NAME_MAX_LENGTH) {
            NotificationService.showError(localizationHandler.get("solar-system-name-too-long"));
            return;
        }

        await Endpoints.SKYXPLORE_SOLAR_SYSTEM_RENAME.createRequest({ value: modifiedName }, { solarSystemId: solarSystemId })
            .send();

        setSolarSystemName(modifiedName);
        setEnableEditing(false);
    }

    return (
        <header id="skyxplore-game-solar-system-header">
            {!enableEditing &&
                <h1>
                    {solarSystemName}
                    <Button
                        id="skyxplore-game-solar-system-name-edit-button"
                        onclick={() => setEnableEditing(true)}
                    />
                </h1>
            }

            {enableEditing &&
                <div className="centered">
                    <InputField
                        id="skyxplore-game-solar-system-name-edit-input"
                        type="text"
                        value={modifiedName}
                        onchangeCallback={setModifiedName}
                        placeholder={localizationHandler.get("solar-system-name")}
                    />

                    <Button
                        id="skyxplore-game-solar-system-name-save-button"
                        label="X"
                        onclick={changeName}
                    />

                    <Button
                        id="skyxplore-game-solar-system-name-discard-button"
                        onclick={() => setEnableEditing(false)}
                        label="X"
                    />
                </div>
            }

            <Button
                id="skyxplore-game-solar-system-close-button"
                label="X"
                onclick={closePage}
            />
        </header>
    );
}

export default SolarSystem;