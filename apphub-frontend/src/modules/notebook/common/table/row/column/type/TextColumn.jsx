import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";

const TextColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    custom = false,
    selectType,
    localizationHandler
}) => {
    const updateContent = (newValue) => {
        columnData.data = newValue;
        updateColumn();
    }
    if (editingEnabled) {
        return (
            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div>
                    <InputField
                        className={"notebook-table-column-input" + (custom ? " notebook-custom-table-column-input" : "")}
                        type="text"
                        onchangeCallback={updateContent}
                        value={columnData.data}
                    />

                    {custom &&
                        <Button
                            className="notebook-table-change-column-type-button"
                            onclick={selectType}
                            title={localizationHandler.get("change-column-type")}
                        />
                    }
                </div>
            </td>
        )
    } else {
        return (
            <td className="table-column">
                {columnData.data}
            </td>
        );
    }
}

export default TextColumn;