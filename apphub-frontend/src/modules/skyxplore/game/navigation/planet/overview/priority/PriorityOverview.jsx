import React, { useEffect, useState } from "react";
import localizationData from "./priority_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import LabelWrappedInputField from "../../../../../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import Button from "../../../../../../../common/component/input/Button";
import { isTrue } from "../../../../../../../common/js/Utils";
import { SKYXPLORE_PLANET_UPDATE_PRIORITY } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const PriorityOverview = ({ priorities, setPriorities, planetId, tabSettings, updateTabSettings }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [displayDetails, setDisplayDetails] = useState(true);

    useEffect(
        () => {
            setDisplayDetails(isTrue(tabSettings.tabs.priorities));
        },
        [tabSettings]
    );

    const updateDisplayDetails = (newValue) => {
        tabSettings.tabs.priorities = newValue;

        updateTabSettings(tabSettings);
        setDisplayDetails(newValue);
    }

    const setValue = async (priorityType, newPriority) => {
        await SKYXPLORE_PLANET_UPDATE_PRIORITY.createRequest({ value: newPriority }, { planetId: planetId, priorityType: priorityType })
            .send();

        const copy = new MapStream(priorities)
            .add(priorityType, newPriority)
            .toObject();
        setPriorities(copy);
    }

    const getPriorities = () => {
        return new MapStream(priorities)
            .sorted((a, b) => localizationHandler.get(a.key).localeCompare(localizationHandler.get(b.key)))
            .toList((priorityType, value) => <PrioritySlider
                key={priorityType}
                priorityType={priorityType}
                value={value}
                localizationHandler={localizationHandler}
                setValue={setValue}
            />)
    }

    return (
        <div id="skyxplore-game-planet-overview-priorities" className="skyxplore-gamep-planet-overview-tab">
            <Button
                className="skyxplore-game-planet-overview-tab-expand-button"
                label={displayDetails ? "-" : "+"}
                onclick={() => updateDisplayDetails(!displayDetails)}
            />

            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            {displayDetails && getPriorities()}
        </div>
    );
}

const PrioritySlider = ({ priorityType, value, localizationHandler, setValue }) => {
    return (
        <div
            id={"skyxplore-game-planet-overview-priority-" + priorityType}
            className="skyxplore-gamep-planet-overview-tab-item"
            title={localizationHandler.get(priorityType + "-title")}
        >

            <LabelWrappedInputField
                preLabel={localizationHandler.get(priorityType) + ": "}
                inputField={
                    <NumberInput
                        type="range"
                        min="1"
                        max="10"
                        value={value}
                        onchangeCallback={(newPriority) => setValue(priorityType, newPriority)}
                    />
                }
                postLabel={value}
            />
        </div>
    );
}

export default PriorityOverview;