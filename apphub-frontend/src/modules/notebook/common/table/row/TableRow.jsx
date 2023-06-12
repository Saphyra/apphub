import React from "react";
import Button from "../../../../../common/component/input/Button";
import MoveDirection from "../../MoveDirection";
import Stream from "../../../../../common/js/collection/Stream";
import TableColumn from "./column/TableColumn";
import "./table_row.css";

const TableRow = ({ rowData, updateRow, removeRow, moveRow, editingEnabled = true }) => {
    const getColumns = () => {
        return new Stream(rowData.columns)
            .sorted((a, b) => a.columnIndex - b.columnIndex)
            .map(column =>
                <TableColumn
                    key={column.columnId}
                    columnData={column}
                    updateColumn={updateRow}
                    editingEnabled={editingEnabled}
                />
            )
            .toList();
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
        <tr>
            {getCommandButtons()}

            {getColumns()}
        </tr>
    );
}

export default TableRow;