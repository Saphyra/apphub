const OrderBy = {
    SYSTEM_DISTANCE: {
        key: "SYSTEM_DISTANCE",
        compare: (a, b) => a.starSystemDistance - b.starSystemDistance
    },
    TRADE_AMOUNT: {
        key: "TRADE_AMOUNT",
        compare: (a, b) => a.tradeAmount - b.tradeAmount
    },
    PRICE: {
        key: "PRICE",
        compare: (a, b) => a.price - b.price
    },
    LAST_UPDATED: {
        key: "LAST_UPDATED",
        compare: (a, b) => b.lastUpdated.localeCompare(a.lastUpdated)
    }
}

export default OrderBy;