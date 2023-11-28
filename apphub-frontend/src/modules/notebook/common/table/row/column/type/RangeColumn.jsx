import React from "react";
import Button from "../../../../../../../common/component/input/Button";
import LabelWrappedInputField from "../../../../../../../common/component/input/LabelWrappedInputField";
import RangeInput from "../../../../../../../common/component/input/RangeInput";
import PreLabeledInputField from "../../../../../../../common/component/input/PreLabeledInputField";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import PostLabeledInputField from "../../../../../../../common/component/input/PostLabeledInputField";

const RangeColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler
}) => {
    const updateValue = (value) => {
        columnData.data.value = value;
        updateColumn();
    }

    const updateStep = (step) => {
        columnData.data.step = step;
        updateColumn();
    }

    const updateMin = (min) => {
        if (min > columnData.data.max) {
            return;
        }

        columnData.data.min = min;

        if (min > columnData.data.value) {
            columnData.data.value = min;
        }

        updateColumn();
    }

    const updateMax = (max) => {
        if (max < columnData.data.min) {
            return;
        }

        columnData.data.max = max;

        if (max < columnData.data.value) {
            columnData.data.value = max;
        }

        updateColumn();
    }

    if (editingEnabled) {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column-wrapper">
                    <div className="table-column-content">
                        <LabelWrappedInputField
                            preLabel={localizationHandler.get("value")}
                            postLabel={columnData.data.value}
                            inputField={
                                <RangeInput
                                    className="notebook-table-column-data-range-value"
                                    placeholder={localizationHandler.get("value")}
                                    onchangeCallback={updateValue}
                                    value={columnData.data.value}
                                    step={columnData.data.step}
                                    min={columnData.data.min}
                                    max={columnData.data.max}
                                />
                            }
                        />

                        <br />

                        <PreLabeledInputField
                            label={localizationHandler.get("step")}
                            input={
                                <NumberInput
                                    className="notebook-table-column-data-number-step"
                                    placeholder={localizationHandler.get("step")}
                                    onchangeCallback={updateStep}
                                    value={columnData.data.step}
                                    min={1}
                                />
                            }
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("min")}
                            input={
                                <NumberInput
                                    className="notebook-table-column-data-number-min"
                                    placeholder={localizationHandler.get("min")}
                                    onchangeCallback={updateMin}
                                    value={columnData.data.min}
                                    max={columnData.data.max}
                                />
                            }
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("max")}
                            input={
                                <NumberInput
                                    className="notebook-table-column-data-number-max"
                                    placeholder={localizationHandler.get("max")}
                                    onchangeCallback={updateMax}
                                    value={columnData.data.max}
                                    min={columnData.data.min}
                                />
                            }
                        />
                    </div>

                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                </div>
            </td>
        );
    } else {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column-wrapper">
                    <div className="table-column-content">
                        <PostLabeledInputField
                            label={columnData.data.value}
                            input={
                                <RangeInput
                                    className="notebook-table-column-data-range-value"
                                    placeholder={localizationHandler.get("value")}
                                    onchangeCallback={updateValue}
                                    value={columnData.data.value}
                                    step={columnData.data.step}
                                    min={columnData.data.min}
                                    max={columnData.data.max}
                                    disabled={true}
                                />
                            }
                        />
                    </div>
                </div>
            </td>
        );
    }
}

export default RangeColumn;