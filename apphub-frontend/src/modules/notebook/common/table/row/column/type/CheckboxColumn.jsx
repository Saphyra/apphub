import React from "react";
import InputField from "../../../../../../../common/component/input/InputField";
import Button from "../../../../../../../common/component/input/Button";
import Utils from "../../../../../../../common/js/Utils";
import Endpoints from "../../../../../../../common/js/dao/dao";

const CheckboxColumn = ({
    columnData,
    updateColumn,
    editingEnabled = true,
    selectType,
    localizationHandler
}) => {
    const updateData = (checked) => {
        if(!editingEnabled){
            Endpoints.NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS.createRequest({value: checked}, {columnId: columnData.columnId})
                .send();
        }

        columnData.data = checked;
        updateColumn();
    }

    return (
        <td className={"table-column notebook-table-column-type-" + columnData.columnType.toLowerCase() + (editingEnabled ? " editable" : "")}>
            <div className="table-column-wrapper">
                <div className="table-column-content">
                    <InputField
                        type="checkbox"
                        onchangeCallback={updateData}
                        checked={Utils.isTrue(columnData.data)}
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