import React from "react";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import Button from "../../../../../../common/component/input/Button";
import NumberInput from "../../../../../../common/component/input/NumberInput";

const Number = ({ data, updateData, selectType, localizationHandler }) => {
    const updateValue = (value) => {
        data.value = value;
        updateData(data);
    }

    const updateStep = (step) => {
        data.step = step;
        updateData(data);
    }

    return (
        <div>
            <PreLabeledInputField
                label={localizationHandler.get("value")}
                input={
                    <NumberInput
                        className="notebook-custom-table-column-data-number-value"
                        placeholder={localizationHandler.get("value")}
                        onchangeCallback={updateValue}
                        value={data.value}
                        step={data.step}
                    />
                }
            />

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

            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default Number;