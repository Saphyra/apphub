import Stream from "../../../../../../common/js/collection/Stream";

const RowIndexRange = {
    MIN: (rows) => new Stream(rows)
        .map(row => row.rowIndex)
        .min()
        .orElse(0) - 1,

    MAX: (rows) => new Stream(rows)
        .map(row => row.rowIndex)
        .max()
        .orElse(0) + 1
}

export default RowIndexRange;