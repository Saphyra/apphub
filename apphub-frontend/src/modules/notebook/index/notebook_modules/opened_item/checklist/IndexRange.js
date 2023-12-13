import Stream from "../../../../../../common/js/collection/Stream";

const IndexRange = {
    MIN: (items) => new Stream(items)
        .map(item => item.index)
        .min()
        .map(i => i - 1)
        .orElse(0),

    MAX: (items) => new Stream(items)
        .map(item => item.index)
        .max()
        .map(i => i + 1)
        .orElse(0)
}

export default IndexRange;