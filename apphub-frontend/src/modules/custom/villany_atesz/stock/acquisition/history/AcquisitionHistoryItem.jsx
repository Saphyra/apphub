import React from "react";
import { numberOfDigits } from "../../../../../../common/js/Utils";

const AcquisitionHistoryItem = ({ item, padding }) => {
    let extraSpaces = "";
    for (let i = numberOfDigits(item.amount); i < padding; i++) {
        extraSpaces += " ";
    }

    return (
        <li className="villany-atesz-stock-acquisition-history-item selectable">
            <span className="villany-atesz-stock-acquisition-history-item-amount">{item.amount}</span>
            <pre>{extraSpaces} x </pre>
            <span className="villany-atesz-stock-acquisition-history-item-name">{item.stockItemName}</span>
        </li>
    );
}

export default AcquisitionHistoryItem;