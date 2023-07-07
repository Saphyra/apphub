import Utils from "../../../../../common/js/Utils";
import CustomTableColumnType from "../CustomTableColumnType";

const CustomTableColumnData = class {
    constructor(columnIndex, type = CustomTableColumnType.EMPTY, data = "", columnId = Utils.generateRandomId()) {
        if (columnIndex === null || columnIndex === undefined) {
            Utils.throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        this.columnIndex = columnIndex;
        this.type = type;
        this.data = data;
        this.columnId = columnId;
    }
}

export default CustomTableColumnData;