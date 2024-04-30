import React from "react";
import Utils from "../../../../../common/js/Utils";

const OverviewItem = ({ item, activeCart }) => {
    return (
        <tr className="villany-atesz-stock-overview-item">
            <td className="villany-atesz-stock-overview-item-category">{item.category.name}</td>
            <td className="villany-atesz-stock-overview-item-name">{item.name}</td>
            <td className="villany-atesz-stock-overview-item-serial-number">{item.serialNumber}</td>
            <td className="villany-atesz-stock-overview-item-in-car">{item.inCar} {item.category.measurement}</td>
            <td className={"villany-atesz-stock-overview-item-in-cart" + item.inCart > 0 ? "in-cart" : ""}>{item.inCart} {item.category.measurement}</td>
            <td className="villany-atesz-stock-overview-item-in-storage">{item.inStorage} {item.category.measurement}</td>
            <td className="villany-atesz-stock-overview-item-price">{item.price} Ft</td>
            <td className="villany-atesz-stock-overview-item-stock-value">{item.price * (item.inCar + item.inStorage)} Ft</td>
            {!Utils.isBlank(activeCart) &&
                <td></td>
            }
        </tr>
    );
}

export default OverviewItem;