import Stream from "../../../../../../common/js/collection/Stream";

const IndexRange = {
    MIN: (items) => new Stream(items)
        .map(item => item.index)
        .min()
        .orElse(0) - 1,

    MAX: (items) => new Stream(items)
        .map(item => item.index)
        .max()
        .orElse(0) + 1
}

export default IndexRange;