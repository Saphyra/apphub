import Utils from "../../../../../common/js/Utils";
import ItemType from "../../ItemType";

const TableRowData = class {
    constructor(rowIndex, columns = [], checked = false, rowId = Utils.generateRandomId(), itemType = ItemType.NEW) {
        if (rowIndex === null || rowIndex === undefined) {
            Utils.throwException("IllegalArgument", "rowIndex must not be " + rowIndex);
        }

        this.rowIndex = rowIndex;
        this.columns = columns;
        this.rowId = rowId;
        this.checked = checked;
        this.itemType = itemType;
    }
}

export default TableRowData;