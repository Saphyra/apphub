import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import Utils from "../../../../../../../common/js/Utils";

const CheckboxColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler
}) => {
    const updateData = (checked) => {
        columnData.data = checked;
        updateColumn();
    }

    return (
        <td className={"notebook-table-column-type-" + columnData.columnType.toLowerCase() + (editingEnabled ? " editable" : "")}>
            <div className="table-column">
                <div className="table-column-content">
                    <InputField
                        type="checkbox"
                        onchangeCallback={updateData}
                        checked={Utils.isTrue(columnData.data)}
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
        </td >
    )
}

export default CheckboxColumn;