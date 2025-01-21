import React from "react";
import SelectInput, { SelectOption } from "../../../common/component/input/SelectInput";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import Button from "../../../common/component/input/Button";

const RefreshIntervalSelector = ({ refreshInterval, setRefreshInterval, localizationHandler, loadCallback }) => {
    return (
        <div id="performance-reporting-refresh-interval-selector-container">
            <PreLabeledInputField
                label={localizationHandler.get("refresh-interval")}
                input={<SelectInput
                    id="performance-reporting-refresh-interval-selector"
                    value={refreshInterval}
                    options={[
                        new SelectOption(localizationHandler.get("off"), 0),
                        new SelectOption(localizationHandler.get("1s"), 1000),
                        new SelectOption(localizationHandler.get("5s"), 5000),
                        new SelectOption(localizationHandler.get("15s"), 15000),
                        new SelectOption(localizationHandler.get("30s"), 30000),
                        new SelectOption(localizationHandler.get("1m"), 60000),
                        new SelectOption(localizationHandler.get("5m"), 300000),
                    ]}
                    onchangeCallback={setRefreshInterval}
                />}
            />

            <Button
                id="performance-reporting-refresh-button"
                label={localizationHandler.get("refresh")}
                onclick={loadCallback}
            />
        </div>
    );
}

export default RefreshIntervalSelector;