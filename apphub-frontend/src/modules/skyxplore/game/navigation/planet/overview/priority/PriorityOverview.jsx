import React from "react";
import localizationData from "./priority_overview_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import MapStream from "../../../../../../../common/js/collection/MapStream";
import LabelWrappedInputField from "../../../../../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import Endpoints from "../../../../../../../common/js/dao/dao";

const PriorityOverview = ({ priorities, setPriorities, planetId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const setValue = async (priorityType, newPriority) => {
        await Endpoints.SKYXPLORE_PLANET_UPDATE_PRIORITY.createRequest({ value: newPriority }, { planetId: planetId, priorityType: priorityType })
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
            <div className="skyxplore-game-planet-overview-tab-title">{localizationHandler.get("tab-title")}</div>

            {getPriorities()}
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