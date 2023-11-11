import Utils from "../../../../../../../common/js/Utils";
import Stream from "../../../../../../../common/js/collection/Stream";
import MoveDirection from "../../../../../common/MoveDirection";
import TableRowData from "../../../../../common/table/row/TableRowData";
import TableColumnData from "../../../../../common/table/row/column/TableColumnData";

export const newRow = (rows, tableHeads, setRows, indexRange) => {
    console.log(rows);
    const rowIndex = indexRange(rows);
    console.log(rowIndex);

    const columns = new Stream(tableHeads)
        .map(tableHead => tableHead.columnIndex)
        .map(columnIndex => new TableColumnData(columnIndex))
        .toList();

    const newRow = new TableRowData(rowIndex, columns);

    const copy = new Stream(rows)
        .add(newRow)
        .toList();
    setRows(copy);
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