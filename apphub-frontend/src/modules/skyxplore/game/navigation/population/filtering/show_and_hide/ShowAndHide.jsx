import React from "react";
import localizationData from "./show_and_hide_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import "./show_and_hide.css";
import citizenLocalizationData from "../../../../common/localization/citizen_localization.json";
import Stream from "../../../../../../../common/js/collection/Stream";
import PostLabeledInputField from "../../../../../../../common/component/input/PostLabeledInputField";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import { SettingType } from "../../../../common/hook/Setting";
import { addAndSet, hasValue, removeAndSet } from "../../../../../../../common/js/Utils";
import { SKYXPLORE_DATA_CREATE_SETTING, SKYXPLORE_DATA_DELETE_SETTING } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";

const ShowAndHide = ({ hiddenProperties, setHiddenProperties, hideSetting, updateHidden, planetId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const citizenLocalizationHandler = new LocalizationHandler(citizenLocalizationData);

    const getProperties = () => {
        return new Stream(citizenLocalizationHandler.getKeys())
            .sorted((a, b) => citizenLocalizationHandler.get(a).localeCompare(citizenLocalizationHandler.get(b)))
            .map(property => <PostLabeledInputField
                className="skyxplore-game-population-show-and-hide-checkbox-label"
                key={property}
                label={citizenLocalizationHandler.get(property)}
                input={
                    <InputField
                        id={"skyxplore-game-population-show-and-hide-checkbox-" + property.toLowerCase()}
                        className="skyxplore-game-population-show-and-hide-checkbox"
                        type="checkbox"
                        checked={hiddenProperties.indexOf(property) < 0}
                        onchangeCallback={(checked) => updateStatus(property, checked)}
                    />
                }
            />)
            .toList();
    }

    const updateStatus = (property, checked) => {
        if (!checked) {
            addAndSet(hiddenProperties, property, setHiddenProperties);
        } else {
            removeAndSet(hiddenProperties, p => p === property, setHiddenProperties);
        }
    }

    const getDeleteSettingButton = () => {
        if (!hasValue(hideSetting)) {
            return;
        } else if (!hasValue(hideSetting.location)) {
            return <Button
                id="skyxplore-game-population-hide-delete-global-default-button"
                label={localizationHandler.get("delete-global-default")}
                onclick={() => deleteSetting(null)}
            />
        } else {
            return <Button
                id="skyxplore-game-population-hide-delete-planet-default-button"
                label={localizationHandler.get("delete-planet-default")}
                onclick={() => deleteSetting(planetId)}
            />
        }
    }

    const saveSetting = (location) => {
        const payload = {
            location: location,
            type: SettingType.POPULATION_HIDE,
            data: hiddenProperties
        }

        SKYXPLORE_DATA_CREATE_SETTING.createRequest(payload)
            .send();
    }

    const deleteSetting = async (location) => {
        const payload = {
            location: location,
            type: SettingType.POPULATION_HIDE,
        }

        const response = await SKYXPLORE_DATA_DELETE_SETTING.createRequest(payload)
            .send();

        updateHidden(response.value);
    }

    return (
        <div className="skyxplore-game-population-filtering-panel">
            <div className="skyxplore-game-population-filtering-panel-title">
                {localizationHandler.get("show-and-hide")}
            </div>

            <div className="skyxplore-game-population-filtering-panel-content">
                <div
                    className="button"
                    id="skyxplore-game-population-show-all-button"
                    onClick={() => setHiddenProperties([])}
                >
                    {localizationHandler.get("show-all")}
                </div>

                <div
                    className="button"
                    id="skyxplore-game-population-hide-all-button"
                    onClick={() => setHiddenProperties(citizenLocalizationHandler.getKeys())}
                >
                    {localizationHandler.get("hide-all")}
                </div>

                {getProperties()}
            </div>

            <div className="skyxplore-game-population-default-buttons">
                <Button
                    id="skyxplore-game-population-hide-save-planet-default-button"
                    label={localizationHandler.get("save-planet-default")}
                    onclick={() => saveSetting(planetId)}
                />
                <Button
                    id="skyxplore-game-population-hide-save-global-default-button"
                    label={localizationHandler.get("save-global-default")}
                    onclick={() => saveSetting(null)}
                />

                {getDeleteSettingButton()}
            </div>
        </div>
    );
}

export default ShowAndHide;