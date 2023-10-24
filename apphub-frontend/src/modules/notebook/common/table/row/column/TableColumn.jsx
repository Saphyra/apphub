import React from "react";
import InputField from "../../../../../../common/component/input/InputField";

const TableColumn = ({ columnData, updateColumn, editingEnabled = true }) => {
    const updateContent = (newValue) => {
        columnData.data = newValue;
        updateColumn();
    }
    if (editingEnabled) {
        return (
            <td className="table-column editable">
                <InputField
                    className="notebook-table-column-input"
                    type="text"
                    onchangeCallback={updateContent}
                    value={columnData.data}
                />
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

export default TableColumn;