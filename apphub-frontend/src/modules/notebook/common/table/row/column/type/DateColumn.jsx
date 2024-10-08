import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";

const DateColumn = ({
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
                <div className="notebook-table-column-wrapper">
                    <div className="notebook-table-column-content">
                        <InputField
                            type="date"
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
                <div className="notebook-table-column-wrapper">
                    {columnData.data}
                </div>
            </td >
        )
    }
}

export default DateColumn;