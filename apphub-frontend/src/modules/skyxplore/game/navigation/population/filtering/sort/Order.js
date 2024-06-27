import Stream from "../../../../../../../common/js/collection/Stream";

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

export const fromName = (name) => {
    return new Stream(Object.keys(Order))
        .filter(orderName => name === orderName)
        .map(orderName => Order[orderName])
        .findFirst()
        .orElseThrow("IllegalArgument", "There is no Order with name " + name);
}

export default Order;