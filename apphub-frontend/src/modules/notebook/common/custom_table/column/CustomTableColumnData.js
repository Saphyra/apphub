import Utils from "../../../../../common/js/Utils";
import ColumnType from "../../table/row/column/type/ColumnType";

const CustomTableColumnData = class {
    constructor(columnIndex, type = ColumnType.EMPTY, data = "", columnId = Utils.generateRandomId()) {
        if (columnIndex === null || columnIndex === undefined) {
            Utils.throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        if(typeof columnIndex !== "number"){
            Utils.throwException("IllegalArgument", "ColumnIndex is not a number.");
        }

        this.columnIndex = columnIndex;
        this.type = type;
        this.data = data;
        this.columnId = columnId;
    }
}

export default CustomTableColumnData;