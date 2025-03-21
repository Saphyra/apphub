const Order = {
    ASCENDING: {
        key: "ASCENDING",
        sorter: i => i
    },
    DESCENDING: {
        key: "DESCENDING",
        sorter: i => -1 * i
    }
}

export default Order;