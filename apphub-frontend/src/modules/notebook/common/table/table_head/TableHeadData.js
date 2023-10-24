import Utils from "../../../../../common/js/Utils"
import ItemType from "../../ItemType";

const TableHeadData = class {
    constructor(columnIndex, content = "", tableHeadId = Utils.generateRandomId(), type = ItemType.NEW) {
        if (columnIndex === null || columnIndex === undefined) {
            Utils.throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        if(typeof columnIndex !== "number"){
            Utils.throwException("IllegalArgument", "ColumnIndex is not a number.");
        }

        this.columnIndex = columnIndex;
        this.content = content;
        this.tableHeadId = tableHeadId;
        this.type = type
    }
}

export default TableHeadData;