import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";

const MonthColumn = ({
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
                    <InputField
                        type="month"
                        onchangeCallback={updateContent}
                        value={columnData.data}
                        disabled={!editingEnabled}
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

export default MonthColumn;