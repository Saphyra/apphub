import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";

const DateTimeColumn = ({
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
    if (editingEnabled) {

        return (

            <td className={"table-column editable notebook-table-column-type-" + columnData.columnType.toLowerCase()}>
                <div className="table-column-wrapper">
                    <div className="table-column-content">
                        <InputField
                            type="datetime-local"
                            onchangeCallback={updateContent}
                            value={columnData.data}
                            disabled={!editingEnabled}
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
            <td className="table-column">
                <div className="table-column-wrapper">
                    {columnData.data.replace("T", " ")}
                </div>
            </td >
        )
    }
}

export default DateTimeColumn;