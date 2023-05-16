import React, { useState } from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import "./settings/settings.css"

const Settings = ({ localizationHandler }) => {
    const [settingsDisplayed, setSettingsDispalyed] = useState(false);

    const getToggleSettingsDisplayedButton = () => {
        return <Button
            id="notebook-toggle-display-settings-button"
            onclick={() => setSettingsDispalyed(!settingsDisplayed)}
            label="V"
            className={settingsDisplayed ? "active" : ""}
        />
    }

    return (
        <div id="notebook-settings">
            <InputField
                id="notebook-search"
                type="text"
                placeholder={localizationHandler.get("search")}
                onchangeCallback={() => {}}
            />

            <Button 
                id="notebook-clear-search"
                onclick={() => {}}
                label="X"
            />

            {getToggleSettingsDisplayedButton()}
        </div>
    );
}

export default Settings;