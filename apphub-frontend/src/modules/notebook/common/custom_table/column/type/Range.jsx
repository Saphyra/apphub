import React from "react";
import PostLabeledInputField from "../../../../../../common/component/input/PostLabeledInputField";
import RangeInput from "../../../../../../common/component/input/RangeInput";
import LabelWrappedInputField from "../../../../../../common/component/input/LabelWrappedInputField";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import NumberInput from "../../../../../../common/component/input/NumberInput";
import Button from "../../../../../../common/component/input/Button";

const Range = ({ data, updateData, selectType, localizationHandler }) => {
    const updateValue = (value) => {
        data.value = value;
        updateData(data);
    }

    const updateStep = (step) => {
        data.step = step;
        updateData(data);
    }

    const updateMin = (min) => {
        if (min > data.max) {
            return;
        }

        data.min = min;

        console.log();
        console.log("Min", min);
        console.log("Value", data.value);
        if (min > data.value) {
            data.value = min;
            console.log("NewValue", data.value);
        }

        updateData(data);
    }

    const updateMax = (max) => {
        if (max < data.min) {
            return;
        }

        data.max = max;

        if (max < data.value) {
            data.value = max;
        }

        updateData(data);
    }

    return (
        <div>
            <LabelWrappedInputField
                preLabel={localizationHandler.get("value")}
                postLabel={data.value}
                inputField={
                    <RangeInput
                        className="notebook-custom-table-column-data-range-value"
                        placeholder={localizationHandler.get("value")}
                        onchangeCallback={updateValue}
                        value={data.value}
                        step={data.step}
                        min={data.min}
                        max={data.max}
                    />
                }
            />

            <br />

            <PreLabeledInputField
                label={localizationHandler.get("step")}
                input={
                    <NumberInput
                        className="notebook-custom-table-column-data-number-step"
                        placeholder={localizationHandler.get("step")}
                        onchangeCallback={updateStep}
                        value={data.step}
                        min={1}
                    />
                }
            />

            <PreLabeledInputField
                label={localizationHandler.get("min")}
                input={
                    <NumberInput
                        className="notebook-custom-table-column-data-number-min"
                        placeholder={localizationHandler.get("min")}
                        onchangeCallback={updateMin}
                        value={data.min}
                        max={data.max}
                    />
                }
            />

            <PreLabeledInputField
                label={localizationHandler.get("max")}
                input={
                    <NumberInput
                        className="notebook-custom-table-column-data-number-max"
                        placeholder={localizationHandler.get("max")}
                        onchangeCallback={updateMax}
                        value={data.max}
                        min={data.min}
                    />
                }
            />

            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default Range;