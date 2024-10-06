import React, { useEffect, useState } from "react";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import SolarSystemConstants from "../SolarSystemConstants";
import Button from "../../../../../../common/component/input/Button";
import InputField from "../../../../../../common/component/input/InputField";
import localizationData from "./solar_system_header_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import "./solar_system_header.css";
import { isBlank } from "../../../../../../common/js/Utils";
import { SKYXPLORE_SOLAR_SYSTEM_RENAME } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const SolarSystemHeader = ({ solarSystemId, solarSystemName, closePage, setSolarSystemName }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [enableEditing, setEnableEditing] = useState(false);
    const [modifiedName, setModifiedName] = useState("");

    useEffect(() => setModifiedName(solarSystemName), [solarSystemName]);

    const changeName = async () => {
        if (isBlank(modifiedName)) {
            NotificationService.showError(localizationHandler.get("solar-system-name-blank"));
            return;
        }

        if (modifiedName.length > SolarSystemConstants.SOLAR_SYSTEM_NAME_MAX_LENGTH) {
            NotificationService.showError(localizationHandler.get("solar-system-name-too-long"));
            return;
        }

        await SKYXPLORE_SOLAR_SYSTEM_RENAME.createRequest({ value: modifiedName }, { solarSystemId: solarSystemId })
            .send();

        setSolarSystemName(modifiedName);
        setEnableEditing(false);
    }

    return (
        <header id="skyxplore-game-solar-system-header">
            {!enableEditing &&
                <h1 id="skyxplore-game-solar-system-name">
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

export default SolarSystemHeader;