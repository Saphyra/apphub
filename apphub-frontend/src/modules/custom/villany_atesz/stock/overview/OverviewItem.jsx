import React, { useState } from "react";
import Utils from "../../../../../common/js/Utils";
import NumberInput from "../../../../../common/component/input/NumberInput";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";

const OverviewItem = ({ localizationHandler, item, activeCart, setItems, setCart }) => {
    const [amount, setAmount] = useState(0);

    const addToCart = async () => {
        //TODO validation
        const response = await Endpoints.VILLANY_ATESZ_ADD_TO_CART.createRequest({ cartId: activeCart, stockItemId: item.stockItemId, amount: amount })
            .send();

        setItems(response.items);
        setCart(response.cart);
        setAmount(0)
    }

    const moveToCar = async () => {
        //TODO validation
        const response = await Endpoints.VILLANY_ATESZ_MOVE_STOCK_TO_CAR.createRequest({ value: amount }, { stockItemId: item.stockItemId })
            .send();

        setItems(response);
        setAmount(0)
    }

    const moveToStorage = async () => {
        //TODO validation
        const response = await Endpoints.VILLANY_ATESZ_MOVE_STOCK_TO_STORAGE.createRequest({ value: amount }, { stockItemId: item.stockItemId })
            .send();

        setItems(response);
        setAmount(0)
    }

    return (
        <tr className="villany-atesz-stock-overview-item">
            <td className="villany-atesz-stock-overview-item-category">{item.category.name}</td>
            <td className="villany-atesz-stock-overview-item-name">{item.name}</td>
            <td className="villany-atesz-stock-overview-item-serial-number">{item.serialNumber}</td>
            <td className="villany-atesz-stock-overview-item-in-car">{item.inCar - item.inCart} {item.category.measurement}</td>
            <td className={"villany-atesz-stock-overview-item-in-cart" + (item.inCart > 0 ? " in-cart" : "")}>{item.inCart} {item.category.measurement}</td>
            <td className="villany-atesz-stock-overview-item-in-storage">{item.inStorage} {item.category.measurement}</td>
            <td className="villany-atesz-stock-overview-item-price">{item.price} Ft</td>
            <td className="villany-atesz-stock-overview-item-stock-value">{item.price * (item.inCar + item.inStorage)} Ft</td>
            <td className="villany-atesz-stock-overview-item-stock-operations">
                <NumberInput
                    className="villany-atesz-stock-overview-item-add-to-cart-amount"
                    placeholder={localizationHandler.get("amount")}
                    value={amount}
                    onchangeCallback={setAmount}
                />

                {!Utils.isBlank(activeCart) &&
                    <Button
                        className="villany-atesz-stock-overview-item-add-to-cart-button"
                        label={localizationHandler.get("add-to-cart")}
                        onclick={addToCart}
                    />
                }

                <Button
                    className="villany-atesz-stock-overview-item-move-to-car-button"
                    label={localizationHandler.get("move-to-car")}
                    onclick={moveToCar}
                />

                <Button
                    className="villany-atesz-stock-overview-item-move-to-storage-button"
                    label={localizationHandler.get("move-to-storage")}
                    onclick={moveToStorage}
                />
            </td>

        </tr>
    );
}

export default OverviewItem;