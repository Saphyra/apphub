import React from "react";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../common/js/collection/MapStream";
import { View } from "../../common/View";
import viewLocalizationData from "../../localization/view_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import localizationData from "../../localization/view_selector_localization.json";

const ViewSelector = ({ view, setView }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const viewLocalizationHandler = new LocalizationHandler(viewLocalizationData);

    return (
        <div id="calendar-view-selector-wrapper">
            <PreLabeledInputField
                label={localizationHandler.get("view")}
                input={<SelectInput
                    id="calendar-view-selector"
                    value={view}
                    onchangeCallback={setView}
                    options={getOptions()}
                />}
            />
        </div>
    );

    function getOptions() {
        return new MapStream(View)
            .toList((viewName) => new SelectOption(viewLocalizationHandler.get(viewName), viewName));
    }
}

export default ViewSelector;