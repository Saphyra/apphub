import React, { useState } from "react";
import Utils from "../../../../../../common/js/Utils";

const AcquisitionHistoryItem = ({ item, padding }) => {
    let extraSpaces = "";
    for (let i = Utils.numberOfDigits(item.amount); i < padding; i++) {
        extraSpaces += " ";
    }

    console.log(padding, extraSpaces.length);

    return (
        <li className="villany-atesz-stock-acquisition-history-item selectable">
            <span className="villany-atesz-stock-acquisition-history-item-amount">{item.amount}</span>
            <pre>{extraSpaces} x </pre>
            <span className="villany-atesz-stock-acquisition-history-item-name">{item.stockItemName}</span>
        </li>
    );
}

export default AcquisitionHistoryItem;