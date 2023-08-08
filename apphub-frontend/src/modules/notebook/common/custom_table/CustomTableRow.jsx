import React from "react";
import Stream from "../../../../common/js/collection/Stream";
import CustomTableColumn from "./column/CustomTableColumn";
import Button from "../../../../common/component/input/Button";
import MoveDirection from "../MoveDirection";
import "./custom_table_row.css";

const CustomTableRow = ({
    rowData,
    updateRow,
    removeRow,
    moveRow,
    setColumnTypeSelectorData,
    editingEnabled = true
}) => {
    const selectColumnType = (columnIndex) => {
        setColumnTypeSelectorData({
            rowIndex: rowData.rowIndex,
            columnIndex: columnIndex
        })
    }

    const getColumns = () => {
        return new Stream(rowData.columns)
            .sorted((a, b) => a.columnIndex - b.columnIndex)
            .map(column =>
                <CustomTableColumn
                    key={column.columnId}
                    columnData={column}
                    updateColumn={updateRow}
                    editingEnabled={editingEnabled}
                    setColumnTypeSelectorData={selectColumnType}
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
        <tr className={"notebook-table-row" + (rowData.checked ? " checked" : "")}>
            {getCommandButtons()}

            {getColumns()}
        </tr>
    );
}

export default CustomTableRow;