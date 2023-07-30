import React from "react";
import InputField from "../../../../../../common/component/input/InputField";

const TableColumn = ({ columnData, updateColumn, editingEnabled = true }) => {
    const updateContent = (newValue) => {
        columnData.content = newValue;
        updateColumn();
    }
    if (editingEnabled) {
        return (
            <td className="table-column editable">
                <InputField
                    className="notebook-table-column-input"
                    type="text"
                    onchangeCallback={updateContent}
                    value={columnData.content}
                />
            </td>
        )
    } else {
        return (
            <td className="table-column">
                {columnData.content}
            </td>
        );
    }

}

export default TableColumn;