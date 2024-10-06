import { throwException } from "../../../../../common/js/Utils";
import Stream from "../../../../../common/js/collection/Stream";
import MoveDirection from "../../../common/MoveDirection";
import TableRowData from "../../../common/table/row/TableRowData";
import TableColumnData from "../../../common/table/row/column/TableColumnData";
import ColumnType from "../../../common/table/row/column/type/ColumnType";

export const newRow = (rows, tableHeads, setRows, indexRange, custom) => {
    const rowIndex = indexRange(rows);

    const columns = new Stream(tableHeads)
        .map(tableHead => tableHead.columnIndex)
        .map(columnIndex => new TableColumnData(columnIndex, getColumnType(rows, rowIndex, columnIndex, custom)))
        .toList();

    const newRow = new TableRowData(rowIndex, columns);

    const copy = new Stream(rows)
        .add(newRow)
        .toList();
    setRows(copy);
}

const getColumnType = (rows, newRowIndex, columnIndex, custom) => {
    if (!custom) {
        return ColumnType.TEXT;
    }

    return new Stream(rows)
        .filter(row => row.rowIndex === newRowIndex - 1)
        .findFirst()
        .map(
            row => new Stream(row.columns)
                .filter(column => column.columnIndex === columnIndex)
                .findFirst()
                .orElseThrow("IllegalState", "Column not found by columnIndex " + columnIndex)
        )
        .map(column => column.columnType)
        .orElse(ColumnType.EMPTY);
}

export const moveRow = (row, moveDirection, rows, updateRows) => {
    const orderedItems = new Stream(rows)
        .sorted((a, b) => a.rowIndex - b.rowIndex)
        .toList();

    const rowPosition = orderedItems.indexOf(row);

    let otherRowPosition;
    switch (moveDirection) {
        case MoveDirection.UP:
            otherRowPosition = rowPosition - 1;
            break;
        case MoveDirection.DOWN:
            otherRowPosition = rowPosition + 1;
            break;
        default:
            throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
    }

    const otherRow = orderedItems[otherRowPosition];

    if (!otherRow) {
        //Item is at the top/bottom of the list and cannot be moved further.
        return;
    }

    const originalRowIndex = row.rowIndex;
    const newRowIndex = otherRow.rowIndex;

    row.rowIndex = newRowIndex;
    otherRow.rowIndex = originalRowIndex;

    updateRows();
}

export const removeRow = (row, rows, setRows) => {
    const copy = new Stream(rows)
        .remove(r => r === row)
        .toList();
    setRows(copy);
}