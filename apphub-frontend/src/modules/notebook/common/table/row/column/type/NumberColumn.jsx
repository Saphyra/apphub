import React from "react";
import PreLabeledInputField from "../../../../../../../common/component/input/PreLabeledInputField";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import Button from "../../../../../../../common/component/input/Button";

const NumberColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler
}) => {
    const updateValue = (value) => {
        columnData.data.value = value;
        updateColumn(columnData);
    }

    const updateStep = (step) => {
        columnData.data.step = step;
        updateColumn(columnData);
    }

    if (editingEnabled) {
        return (
            <td className={"editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column">
                    <div className="table-column-content">
                        <PreLabeledInputField
                            label={localizationHandler.get("value")}
                            input={
                                <NumberInput
                                    className="notebook-table-column-data-number-value"
                                    placeholder={localizationHandler.get("value")}
                                    onchangeCallback={updateValue}
                                    value={columnData.data.value}
                                    step={columnData.data.step}
                                />
                            }
                        />

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
                    </div>

                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                </div>
            </td>
        )
    } else {
        return (
            <td className={"notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column">
                    {columnData.data.value}
                </div>
            </td>
        );
    }
}

export default NumberColumn;