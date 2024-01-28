import React, { useState } from "react";
import localizationData from "./show_and_hide_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import "./show_and_hide.css";
import citizenLocalizationData from "../../../../common/localization/citizen_localization.json";
import Stream from "../../../../../../../common/js/collection/Stream";
import PostLabeledInputField from "../../../../../../../common/component/input/PostLabeledInputField";
import InputField from "../../../../../../../common/component/input/InputField";
import Utils from "../../../../../../../common/js/Utils";

const ShowAndHide = ({ hiddenProperties, setHiddenProperties }) => {
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
            Utils.addAndSet(hiddenProperties, property, setHiddenProperties);
        } else {
            Utils.removeAndSet(hiddenProperties, p => p === property, setHiddenProperties);
        }
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
        </div>
    );
}

export default ShowAndHide;