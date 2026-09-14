import { useEffect, useState } from "react";
import PanelTitle from "./PanelTitle";
import Setting from "./settings/Setting";
import NumberInputField from "common/component/input/NumberInputField";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import { SKYXPLORE_LOBBY_EDIT_SETTINGS } from "../SkyXploreLobbyEndpoints";
import { ADDITIONAL_SOLAR_SYSTEMS_MAX, ADDITIONAL_SOLAR_SYSTEMS_MIN, MAX_PLAYERS_PER_SOLAR_SYSTEM, MIN_PLAYERS_PER_SOLAR_SYSTEM, PLANET_SIZE_MAX, PLANET_SIZE_MIN, PLANETS_PER_SOLAR_SYSTEM_MAX, PLANETS_PER_SOLAR_SYSTEM_MIN } from "../SkyXploreLobbyConstants";

const Settings = ({ localizationHandler, isHost, settings, setDisplaySpinner }) => {
    const [maxPlayersPerSolarSystem, setMaxPlayersPerSolarSystem] = useState(0);
    const [additionalSolarSystemsMin, setAdditionalSolarSystemsMin] = useState(0);
    const [additionalSolarSystemsMax, setAdditionalSolarSystemsMax] = useState(0);
    const [planetsPerSolarSystemMin, setPlanetsPerSolarSystemMin] = useState(0);
    const [planetsPerSolarSystemMax, setPlanetsPerSolarSystemMax] = useState(0);
    const [planetSizeMin, setPlanetSizeMin] = useState(0);
    const [planetSizeMax, setPlanetSizeMax] = useState(0);
    const [shouldSendToServer, setShouldSendToServer] = useState(false);

    useEffect(() => sendSettingsToServer(), [shouldSendToServer]);
    useEffect(() => updateAll(), [settings]);

    const updateAll = () => {
        setMaxPlayersPerSolarSystem(settings.maxPlayersPerSolarSystem);
        setAdditionalSolarSystemsMin(settings.additionalSolarSystems.min);
        setAdditionalSolarSystemsMax(settings.additionalSolarSystems.max);
        setPlanetsPerSolarSystemMin(settings.planetsPerSolarSystem.min);
        setPlanetsPerSolarSystemMax(settings.planetsPerSolarSystem.max);
        setPlanetSizeMin(settings.planetSize.min);
        setPlanetSizeMax(settings.planetSize.max);
    }

    //Update
    const updateMaxPlayersPerSolarSystem = (event) => {
        let value = Number(event.target.value);

        if (value > MAX_PLAYERS_PER_SOLAR_SYSTEM) {
            value = MAX_PLAYERS_PER_SOLAR_SYSTEM;
        }

        if (value < MIN_PLAYERS_PER_SOLAR_SYSTEM) {
            value = MIN_PLAYERS_PER_SOLAR_SYSTEM;
        }
        setMaxPlayersPerSolarSystem(value);
        setShouldSendToServer(true);
    }

    const updateAdditionalSolarSystemsMin = (event) => {
        let value = Number(event.target.value);

        if (value < ADDITIONAL_SOLAR_SYSTEMS_MIN) {
            value = ADDITIONAL_SOLAR_SYSTEMS_MIN;
        }

        if (value > additionalSolarSystemsMax) {
            value = additionalSolarSystemsMax;
        }

        setAdditionalSolarSystemsMin(value);
        setShouldSendToServer(true);
    }

    const updateAdditionalSolarSystemsMax = (event) => {
        let value = Number(event.target.value);

        if (value > ADDITIONAL_SOLAR_SYSTEMS_MAX) {
            value = ADDITIONAL_SOLAR_SYSTEMS_MAX;
        }

        if (value < additionalSolarSystemsMin) {
            value = additionalSolarSystemsMin;
        }

        setAdditionalSolarSystemsMax(value);
        setShouldSendToServer(true);
    }

    const updatePlanetsPerSolarSystemMin = (event) => {
        let value = Number(event.target.value);

        if (value < PLANETS_PER_SOLAR_SYSTEM_MIN) {
            value = PLANETS_PER_SOLAR_SYSTEM_MIN;
        }

        if (value > planetsPerSolarSystemMax) {
            value = planetsPerSolarSystemMax;
        }

        setPlanetsPerSolarSystemMin(value);
        setShouldSendToServer(true);
    }

    const updatePlanetsPerSolarSystemMax = (event) => {
        let value = Number(event.target.value);

        if (value > PLANETS_PER_SOLAR_SYSTEM_MAX) {
            value = PLANETS_PER_SOLAR_SYSTEM_MAX;
        }

        if (value < planetsPerSolarSystemMin) {
            value = planetsPerSolarSystemMin;
        }

        setPlanetsPerSolarSystemMax(value);
        setShouldSendToServer(true);
    }

    const updatePlanetSizeMin = (event) => {
        let value = Number(event.target.value);

        if (value < PLANET_SIZE_MIN) {
            value = PLANET_SIZE_MIN;
        }

        if (value > planetSizeMax) {
            value = planetSizeMax;
        }

        setPlanetSizeMin(value);
        setShouldSendToServer(true);
    }

    const updatePlanetSizeMax = (event) => {
        let value = Number(event.target.value);

        if (value > PLANET_SIZE_MAX) {
            value = PLANET_SIZE_MAX;
        }

        if (value < planetSizeMin) {
            value = planetSizeMin;
        }

        setPlanetSizeMax(value);
        setShouldSendToServer(true);
    }

    const sendSettingsToServer = () => {
        if (!shouldSendToServer) {
            return;
        }

        const request = {
            maxPlayersPerSolarSystem: maxPlayersPerSolarSystem,
            additionalSolarSystems: {
                min: additionalSolarSystemsMin,
                max: additionalSolarSystemsMax
            },
            planetsPerSolarSystem: {
                min: planetsPerSolarSystemMin,
                max: planetsPerSolarSystemMax
            },
            planetSize: {
                min: planetSizeMin,
                max: planetSizeMax
            }
        }

        SKYXPLORE_LOBBY_EDIT_SETTINGS.createRequest(request)
            .send(setDisplaySpinner);

        setShouldSendToServer(false);
    }

    return (
        <div id="skyxplore-lobby-settings" className="skyxplore-lobby-panel">
            <PanelTitle label={localizationHandler.get("settings")} />
            <div className="skyxplore-lobby-panel-content">
                <Setting
                    label={localizationHandler.get("max-players-per-solar-system")}
                    content={
                        <NumberInputField
                            id="skyxplore-lobby-max-players-per-solar-system"
                            className="skyxplore-lobby-settings-input"
                            value={maxPlayersPerSolarSystem}
                            min={MIN_PLAYERS_PER_SOLAR_SYSTEM}
                            max={MAX_PLAYERS_PER_SOLAR_SYSTEM}
                            onchange={updateMaxPlayersPerSolarSystem}
                            disabled={!isHost}
                        />
                    }
                />

                <Setting
                    label={localizationHandler.get("additional-solar-systems")}
                    content={[
                        <PreLabeledInputField
                            key="min"
                            label={localizationHandler.get("min") + ":"}
                            input={
                                <NumberInputField
                                    id="skyxplore-lobby-additional-solar-systems-min"
                                    className="skyxplore-lobby-settings-input"
                                    value={additionalSolarSystemsMin}
                                    min={ADDITIONAL_SOLAR_SYSTEMS_MIN}
                                    max={additionalSolarSystemsMax}
                                    onchange={updateAdditionalSolarSystemsMin}
                                    disabled={!isHost}
                                />
                            }
                        />,
                        <PreLabeledInputField
                            key="max"
                            label={localizationHandler.get("max") + ":"}
                            input={
                                <NumberInputField
                                    id="skyxplore-lobby-additional-solar-systems-max"
                                    className="skyxplore-lobby-settings-input"
                                    value={additionalSolarSystemsMax}
                                    min={additionalSolarSystemsMin}
                                    max={ADDITIONAL_SOLAR_SYSTEMS_MAX}
                                    onchange={updateAdditionalSolarSystemsMax}
                                    disabled={!isHost}
                                />
                            }
                        />
                    ]}
                />

                <Setting
                    label={localizationHandler.get("planets-per-solar-system")}
                    content={[
                        <PreLabeledInputField
                            key="min"
                            label={localizationHandler.get("min") + ":"}
                            input={
                                <NumberInputField
                                    id="skyxplore-lobby-planets-per-solar-system-min"
                                    className="skyxplore-lobby-settings-input"
                                    value={planetsPerSolarSystemMin}
                                    min={PLANETS_PER_SOLAR_SYSTEM_MIN}
                                    max={planetsPerSolarSystemMax}
                                    onchange={updatePlanetsPerSolarSystemMin}
                                    disabled={!isHost}
                                />
                            }
                        />,
                        <PreLabeledInputField
                            key="max"
                            label={localizationHandler.get("max") + ":"}
                            input={
                                <NumberInputField
                                    id="skyxplore-lobby-planets-per-solar-system-max"
                                    className="skyxplore-lobby-settings-input"
                                    value={planetsPerSolarSystemMax}
                                    min={planetsPerSolarSystemMin}
                                    max={PLANETS_PER_SOLAR_SYSTEM_MAX}
                                    onchange={updatePlanetsPerSolarSystemMax}
                                    disabled={!isHost}
                                />
                            }
                        />
                    ]}
                />

                <Setting
                    label={localizationHandler.get("planet-size")}
                    content={[
                        <PreLabeledInputField
                            key="min"
                            label={localizationHandler.get("min") + ":"}
                            input={
                                <NumberInputField
                                    id="skyxplore-lobby-planet-size-min"
                                    className="skyxplore-lobby-settings-input"
                                    value={planetSizeMin}
                                    min={PLANET_SIZE_MIN}
                                    max={planetSizeMax}
                                    onchange={updatePlanetSizeMin}
                                    disabled={!isHost}
                                />
                            }
                        />,
                        <PreLabeledInputField
                            key="max"
                            label={localizationHandler.get("max") + ":"}
                            input={
                                <NumberInputField
                                    id="skyxplore-lobby-planet-size-max"
                                    className="skyxplore-lobby-settings-input"
                                    value={planetSizeMax}
                                    min={planetSizeMin}
                                    max={PLANET_SIZE_MAX}
                                    onchange={updatePlanetSizeMax}
                                    disabled={!isHost}
                                />
                            }
                        />
                    ]}
                />
            </div>
        </div>
    );
}

export default Settings;