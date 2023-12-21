import Utils from "../../../../../../../common/js/Utils";
import Stream from "../../../../../../../common/js/collection/Stream";
import MoveDirection from "../../../../../common/MoveDirection";
import TableRowData from "../../../../../common/table/row/TableRowData";
import TableColumnData from "../../../../../common/table/row/column/TableColumnData";
import ColumnType from "../../../../../common/table/row/column/type/ColumnType";
import RowIndexRange from "../../../../../common/table/row/RowIndexRange";

export const newRow = (rows, tableHeads, setRows, indexRange, custom) => {
    const rowIndex = indexRange(rows);

    const columns = new Stream(tableHeads)
        .map(tableHead => tableHead.columnIndex)
        .map(columnIndex => new TableColumnData(columnIndex, getColumnType(rows, indexRange, custom, columnIndex)))
        .toList();

    const newRow = new TableRowData(rowIndex, columns);

    const copy = new Stream(rows)
        .add(newRow)
        .toList();
    setRows(copy);
}

const getColumnType = (rows, indexRange, custom, columnIndex) => {
    if (custom) {
        if (indexRange == RowIndexRange.MAX) {
            return new Stream(rows)
                .max(row => row.rowIndex)
                .map(
                    rowIndex => new Stream(rows)
                        .filter(row => row.rowIndex === rowIndex)
                        .findFirst()
                        .orElseThrow("IllegalState", "Row not found with rowIndex " + rowIndex)
                )
                .map(row => row.columns)
                .map(
                    columns => new Stream(columns)
                        .filter(column => column.columnIndex === columnIndex)
                        .findFirst()
                        .map(column => column.columnType)
                        .orElseThrow("IllegalState", "Column not found by columnIndex " + columnIndex)
                )
                .orElse(ColumnType.EMPTY);
        }

        return ColumnType.EMPTY;
    }

    return ColumnType.TEXT;
}

export const moveRow = (row, moveDirection, rows, setRows) => {
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
            Utils.throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
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

    Utils.copyAndSet(rows, setRows);
}

export const removeRow = (row, rows, setRows) => {
    const copy = new Stream(rows)
        .remove(r => r === row)
        .toList();
    setRows(copy);
}