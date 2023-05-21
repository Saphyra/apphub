import Utils from "../../../../../common/js/Utils"

const TableHeadData = class {
    constructor(columnIndex, content = "", tableHeadId = Utils.generateRandomId()) {
        if (columnIndex === null || columnIndex === undefined) {
            Utils.throwException("IllegalArgument", "columnIndex must not be " + columnIndex);
        }

        this.columnIndex = columnIndex;
        this.content = content;
        this.tableHeadId = tableHeadId;
    }
}

export default TableHeadData;