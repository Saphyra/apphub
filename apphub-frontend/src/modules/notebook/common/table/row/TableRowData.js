import Utils from "../../../../../common/js/Utils";

const TableRowData = class {
    constructor(rowIndex, columns = [], checked = false, rowId = Utils.generateRandomId()) {
        if (rowIndex === null || rowIndex === undefined) {
            Utils.throwException("IllegalArgument", "rowIndex must not be " + rowIndex);
        }

        this.rowIndex = rowIndex;
        this.columns = columns;
        this.rowId = rowId;
        this.checked = checked;
    }
}

export default TableRowData;