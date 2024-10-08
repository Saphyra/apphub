import Stream from "../../../../../../../common/js/collection/Stream";
import MoveDirection from "../../../../../common/MoveDirection";
import TableColumnData from "../../../../../common/table/row/column/TableColumnData";
import TableHeadData from "../../../../../common/table/table_head/TableHeadData";
import ColumnType from "../../../../../common/table/row/column/type/ColumnType";
import { copyAndSet, generateRandomId, throwException } from "../../../../../../../common/js/Utils";

export const newColumn = (tableHeads, setTableHeads, rows, setRows, indexRange, custom) => {
    const columnIndex = indexRange(tableHeads);

    const newTableHead = new TableHeadData(columnIndex, "", generateRandomId());
    const copy = new Stream(tableHeads)
        .add(newTableHead)
        .toList();
    setTableHeads(copy);

    new Stream(rows)
        .forEach(row => {
            const newColumn = new TableColumnData(newTableHead.columnIndex, custom ? ColumnType.EMPTY : ColumnType.TEXT);
            row.columns.push(newColumn);
        });

    copyAndSet(rows, setRows);
}

export const moveColumn = (tableHead, moveDirection, tableHeads, setTableHeads, rows, setRows) => {
    const orderedItems = new Stream(tableHeads)
        .sorted((a, b) => a.columnIndex - b.columnIndex)
        .toList();

    const tableHeadIndex = orderedItems.indexOf(tableHead);

    let otherTableHeadIndex;
    switch (moveDirection) {
        case MoveDirection.LEFT:
            otherTableHeadIndex = tableHeadIndex - 1;
            break;
        case MoveDirection.RIGHT:
            otherTableHeadIndex = tableHeadIndex + 1;
            break;
        default:
            throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
    }

    const otherTableHead = orderedItems[otherTableHeadIndex];

    if (!otherTableHead) {
        //Item is at the most left/right of the list and cannot be moved further.
        return;
    }

    const originalColumnIndex = tableHead.columnIndex;
    const newColumnIndex = otherTableHead.columnIndex;

    tableHead.columnIndex = newColumnIndex;
    otherTableHead.columnIndex = originalColumnIndex;

    copyAndSet(tableHeads, setTableHeads);

    new Stream(rows)
        .forEach(row => {
            const originalColumn = new Stream(row.columns)
                .filter(column => column.columnIndex === originalColumnIndex)
                .findFirst()
                .orElseThrow("IllegalState", "Column not found with columnIndex " + originalColumnIndex);

            const otherColumn = new Stream(row.columns)
                .filter(column => column.columnIndex === newColumnIndex)
                .findFirst()
                .orElseThrow("IllegalState", "Column not found with columnIndex " + newColumnIndex);

            originalColumn.columnIndex = newColumnIndex;
            otherColumn.columnIndex = originalColumnIndex;
        });
    
    copyAndSet(rows, setRows);
}

export const removeColumn = (tableHead, tableHeads, setTableHeads, rows, setRows) => {
    const copy = new Stream(tableHeads)
        .remove(i => i === tableHead)
        .toList();
    setTableHeads(copy);

    new Stream(rows)
        .forEach(row => {
            const columns = new Stream(row.columns)
                .remove(column => column.columnIndex === tableHead.columnIndex)
                .toList();
            row.columns = columns;
        });

    copyAndSet(rows, setRows);
}