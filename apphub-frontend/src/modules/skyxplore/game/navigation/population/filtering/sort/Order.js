const Order = {
    ASCENDING: {
        name: "ASCENDING",
        modify: comparationResult => comparationResult
    },
    DESCENDING: {
        name: "DESCENDING",
        modify: comparationResult => -1 * comparationResult
    }
}

export default Order;