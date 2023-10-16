import Utils from "../../../../../../common/js/Utils";
import ItemType from "../../../ItemType";
import ColumnType from "../../ColumnType";

const TableColumnData = class {
    constructor(columnIndex, data = "", columnType = ColumnType.TEXT, columnId = Utils.generateRandomId(), itemType = ItemType.NEW) {
        if (columnIndex === null || columnIndex === undefined) {
            Utils.throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        this.columnIndex = columnIndex;
        this.data = data;
        this.columnId = columnId;
        this.columnType = columnType;
        this.itemType = itemType;
    }
}

export default TableColumnData;