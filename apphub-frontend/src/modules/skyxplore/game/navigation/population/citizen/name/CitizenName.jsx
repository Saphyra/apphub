import React, { useEffect, useState } from "react";
import "./citizen_name.css";
import Button from "../../../../../../../common/component/input/Button";
import InputField from "../../../../../../../common/component/input/InputField";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import localizationData from "./citizen_name_localization.json";
import NotificationService from "../../../../../../../common/js/notification/NotificationService";
import { isBlank } from "../../../../../../../common/js/Utils";
import { SKYXPLORE_PLANET_RENAME_CITIZEN } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const CitizenName = ({ citizenId, name }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [editingEnabled, setEditingEnabled] = useState(false);
    const [modifiedName, setModifiedName] = useState("");

    useEffect(() => setModifiedName(name), [name]);

    const renameCitizen = async () => {
        if (isBlank(modifiedName)) {
            NotificationService.showError(localizationHandler.get("citizen-name-blank"));
            return;
        }

        if (modifiedName.length > 30) {
            NotificationService.showError(localizationHandler.get("citizen-name-too-long"));
            return;
        }

        await SKYXPLORE_PLANET_RENAME_CITIZEN.createRequest({ value: modifiedName }, { citizenId: citizenId })
            .send();

        setEditingEnabled(false);
    }

    if (editingEnabled) {
        return (
            <div className="skyxplore-game-population-citizen-name">
                <InputField
                    type="text"
                    className="skyxplore-game-population-citizen-name-input"
                    placeholder={localizationHandler.get("name")}
                    value={modifiedName}
                    onchangeCallback={setModifiedName}
                />

                <Button
                    className="skyxplore-game-population-citizen-name-save-button"
                    onclick={renameCitizen}
                    placeholder={localizationHandler.get("rename")}
                    label="."
                />

                <Button
                    className="skyxplore-game-population-citizen-name-discard-button"
                    onclick={() => setEditingEnabled(false)}
                    placeholder={localizationHandler.get("discard")}
                    label="X"
                />
            </div>
        );
    } else {
        return (
            <div className="skyxplore-game-population-citizen-name-wrapper">
                <span className="skyxplore-game-population-citizen-name">{name}</span>

                <Button
                    className="skyxplore-game-population-citizen-rename-button"
                    onclick={() => setEditingEnabled(true)}
                />
            </div>
        )
    }
}

export default CitizenName;