import React, { useEffect, useState } from "react";
import localizationData from "./planet_header_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Button from "../../../../../../common/component/input/Button";
import InputField from "../../../../../../common/component/input/InputField";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import PlanetConstants from "../PlanetConstants";
import Endpoints from "../../../../../../common/js/dao/dao";
import "./planet_header.css";
import { isBlank } from "../../../../../../common/js/Utils";

const PlanetHeader = ({ planetId, planetName, setPlanetName, closePage }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [enableEditing, setEnableEditing] = useState(false);
    const [modifiedName, setModifiedName] = useState("");

    useEffect(() => setModifiedName(planetName), [planetName]);

    const changeName = async () => {
        if (isBlank(modifiedName)) {
            NotificationService.showError(localizationHandler.get("planet-name-blank"));
            return;
        }

        if (modifiedName.length > PlanetConstants.PLANET_NAME_MAX_LENGTH) {
            NotificationService.showError(localizationHandler.get("planet-name-too-long"));
            return;
        }

        await Endpoints.SKYXPLORE_PLANET_RENAME.createRequest({ value: modifiedName }, { planetId: planetId })
            .send();

        setPlanetName(modifiedName);
        setEnableEditing(false);
    }

    return (
        <header id="skyxplore-game-planet-header">
            {!enableEditing &&
                <h1 id="skyxplore-game-planet-name">
                    {planetName}
                    <Button
                        id="skyxplore-game-planet-name-edit-button"
                        onclick={() => setEnableEditing(true)}
                    />
                </h1>
            }

            {enableEditing &&
                <div className="centered">
                    <InputField
                        id="skyxplore-game-planet-name-edit-input"
                        type="text"
                        value={modifiedName}
                        onchangeCallback={setModifiedName}
                        placeholder={localizationHandler.get("planet-name")}
                    />

                    <Button
                        id="skyxplore-game-planet-name-save-button"
                        label="X"
                        onclick={changeName}
                    />

                    <Button
                        id="skyxplore-game-planet-name-discard-button"
                        onclick={() => setEnableEditing(false)}
                        label="X"
                    />
                </div>
            }

            <Button
                id="skyxplore-game-planet-close-button"
                label="X"
                onclick={closePage}
            />
        </header>
    )
}

export default PlanetHeader;