import React from "react";
import Button from "../../../../../common/component/input/Button";
import MoveDirection from "../../MoveDirection";
import Stream from "../../../../../common/js/collection/Stream";
import TableColumn from "./column/TableColumn";
import "./table_row.css";
import InputField from "../../../../../common/component/input/InputField";

const TableRow = ({ rowData, updateRow, updateChecked, removeRow, moveRow, editingEnabled = true, checklist = false, custom = false, addFile }) => {
    const addFileToColum = (columnIndex, file) => {
        addFile(rowData.rowIndex, columnIndex, file);
    }

    const getColumns = () => {
        return new Stream(rowData.columns)
            .sorted((a, b) => a.columnIndex - b.columnIndex)
            .map(column =>
                <TableColumn
                    key={column.columnId}
                    columnData={column}
                    updateColumn={updateRow}
                    editingEnabled={editingEnabled}
                    custom={custom}
                    addFileToColum={addFileToColum}
                />
            )
            .toList();
    }

    const getCheckbox = () => {
        return (
            <td className="notebook-table-row-checked-cell">
                <InputField
                    className="notebook-table-row-checked"
                    type="checkbox"
                    onchangeCallback={(checked) => {
                        rowData.checked = checked;
                        updateChecked(rowData);
                    }}
                    checked={rowData.checked}
                />
            </td>
        )
    }

    const getCommandButtons = () => {
        if (editingEnabled) {
            return (
                <td className="notebook-table-row-command-buttons">
                    <Button
                        className="notebook-table-row-move-up-button"
                        label="^"
                        onclick={() => moveRow(rowData, MoveDirection.UP)}
                    />

                    <Button
                        className="notebook-table-row-move-down-button"
                        label="v"
                        onclick={() => moveRow(rowData, MoveDirection.DOWN)}
                    />

                    <Button
                        className="notebook-table-row-remove-button"
                        label="X"
                        onclick={() => removeRow(rowData)}
                    />
                </td>
            );
        }
    }

    return (
        <tr className={"notebook-table-row" + (rowData.checked ? " checked" : "")}>
            {getCommandButtons()}

            {checklist && getCheckbox()}

            {getColumns()}
        </tr>
    );
}

export default TableRow;