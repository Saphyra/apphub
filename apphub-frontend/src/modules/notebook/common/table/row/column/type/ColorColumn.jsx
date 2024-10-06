import React from "react";
import PreLabeledInputField from "../../../../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";

const ColorColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler
}) => {
    const updateContent = (newValue) => {
        columnData.data = newValue;
        updateColumn();
    }

    return (
        <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
            <div className="notebook-table-column-wrapper">
                <div className="notebook-table-column-content">
                    <PreLabeledInputField
                        label={columnData.data}
                        input={
                            <InputField
                                type="color"
                                onchangeCallback={updateContent}
                                value={columnData.data}
                                disabled={!editingEnabled}
                            />
                        }
                    />
                </div>

                {editingEnabled &&
                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                }
            </div>
        </td>
    )
}

export default ColorColumn;