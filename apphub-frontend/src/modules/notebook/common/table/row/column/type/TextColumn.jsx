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
            <td className={"editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column">
                    <div className="table-column-content">
                    <InputField
                        className={"notebook-table-column-input" + (custom ? " notebook-table-column-input" : "")}
                        type="text"
                        onchangeCallback={updateContent}
                        value={columnData.data}
                    />
                    </div>

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
            <td>
                <div className="table-column">
                    {columnData.data}
                </div>
            </td >
        );
    }
}

export default TextColumn;