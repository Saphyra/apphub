import React from "react";
import InputField from "../../../../../../common/component/input/InputField";

const TableColumn = ({ columnData, updateColumn }) => {
    const updateContent = (newValue) => {
        columnData.content = newValue;
        updateColumn();
    }

    return (
        <td>
            <InputField
                className="noteabook-table-column-input"
                type="text"
                onchangeCallback={updateContent}
                value={columnData.content}
            />
        </td>
    )
}

export default TableColumn;