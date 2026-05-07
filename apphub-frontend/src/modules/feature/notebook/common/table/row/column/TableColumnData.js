import { generateRandomId, throwException } from "common/js/Utils";
import ColumnType from "./type/ColumnType";
import ItemType from "../../../ItemType";
import getDefaultDataForColumnType from "./DefaultColumnValueProvider";

const TableColumnData = class {
    constructor(columnIndex, columnType = ColumnType.TEXT, data = undefined, columnId = generateRandomId(), itemType = ItemType.NEW) {
        if (columnIndex === null || columnIndex === undefined) {
            throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        this.columnIndex = columnIndex;
        this.data = data == undefined ? getDefaultDataForColumnType(columnType) : data;
        this.columnId = columnId;
        this.columnType = columnType;
        this.itemType = itemType;
    }
}

export default TableColumnData;