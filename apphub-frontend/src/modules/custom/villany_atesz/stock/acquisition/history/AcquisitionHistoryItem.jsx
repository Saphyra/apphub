import React, { useState } from "react";
import useCache from "../../../../../../common/hook/Cache";
import Endpoints from "../../../../../../common/js/dao/dao";

const AcquisitionHistoryItem = ({ item }) => {
    const [stockItem, setStockItem] = useState({});

    useCache(
        item.stockItemId,
        Endpoints.VILLANY_ATESZ_GET_STOCK_ITEM.createRequest(null, { stockItemId: item.stockItemId }),
        setStockItem
    )

    return (
        <li className="villany-atesz-stock-acquisition-history-item">
            <span className="villany-atesz-stock-acquisition-history-item-amount">{item.amount}</span>
            <span> x </span>
            <span className="villany-atesz-stock-acquisition-history-item-name">{stockItem.name}</span>
        </li>
    );
}

export default AcquisitionHistoryItem;