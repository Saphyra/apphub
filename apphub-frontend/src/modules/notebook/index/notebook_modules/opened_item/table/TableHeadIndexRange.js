import Stream from "../../../../../../common/js/collection/Stream";

const TableHeadIndexRange = {
    MIN: (tableHeads) => new Stream(tableHeads)
        .map(tableHead => tableHead.columnIndex)
        .min()
        .orElse(0) - 1,

    MAX: (tableHeads) => new Stream(tableHeads)
        .map(tableHead => tableHead.columnIndex)
        .max()
        .orElse(0) + 1
}

export default TableHeadIndexRange;