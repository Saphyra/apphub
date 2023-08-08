import Utils from "../../../../../../common/js/Utils";

const TableColumnData = class {
    constructor(columnIndex, content = "", columnId = Utils.generateRandomId()) {
        if (columnIndex === null || columnIndex === undefined) {
            Utils.throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        this.columnIndex = columnIndex;
        this.content = content;
        this.columnId = columnId;
    }
}

export default TableColumnData;