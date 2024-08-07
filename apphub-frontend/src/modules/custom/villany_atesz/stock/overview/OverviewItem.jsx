import React, { useState } from "react";
import Utils from "../../../../../common/js/Utils";
import NumberInput from "../../../../../common/component/input/NumberInput";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";
import { validateOverviewAmount } from "../../validation/VillanyAteszValidation";
import NotificationService from "../../../../../common/js/notification/NotificationService";

const OverviewItem = ({ localizationHandler, item, activeCart, setItems, setCart, setSearch }) => {
    const [amount, setAmount] = useState(1);

    const addToCart = async () => {
        const validationResult = validateOverviewAmount(amount);
        if (!validationResult.valid) {
            NotificationService.showError(validationResult.message);
            return;
        }

        const response = await Endpoints.VILLANY_ATESZ_ADD_TO_CART.createRequest({ cartId: activeCart, stockItemId: item.stockItemId, amount: amount })
            .send();

        setItems(response.items);
        setCart(response.cart);
        setAmount(1);
        setSearch("");
    }

    const inCar = item.inCar - item.inCart > 0 ? (item.inCar - item.inCart + " " + item.category.measurement) : "";
    const inCart = item.inCart > 0 ? (item.inCart + " " + item.category.measurement) : "";
    const inStorage = item.inStorage > 0 ? (item.inStorage + " " + item.category.measurement) : "";

    return (
        <tr className="villany-atesz-stock-overview-item">
            <td className="villany-atesz-stock-overview-item-category">{item.category.name}</td>
            <td className="villany-atesz-stock-overview-item-name">{item.name}</td>
            <td className="villany-atesz-stock-overview-item-serial-number">{item.serialNumber}</td>
            <td className="villany-atesz-stock-overview-item-in-car">{inCar}</td>
            <td className={"villany-atesz-stock-overview-item-in-cart" + (item.inCart > 0 ? " in-cart" : "")}>{inCart}</td>
            <td className="villany-atesz-stock-overview-item-in-storage">{inStorage}</td>
            <td className="villany-atesz-stock-overview-item-price">{item.price} Ft</td>
            <td className="villany-atesz-stock-overview-item-stock-value">{item.price * (item.inCar + item.inStorage)} Ft</td>
            {!Utils.isBlank(activeCart) &&
                <td className="villany-atesz-stock-overview-item-stock-operations">
                    <NumberInput
                        className="villany-atesz-stock-overview-item-add-to-cart-amount"
                        placeholder={localizationHandler.get("amount")}
                        value={amount}
                        onchangeCallback={setAmount}
                    />


                    <Button
                        className="villany-atesz-stock-overview-item-add-to-cart-button"
                        label={localizationHandler.get("add-to-cart")}
                        onclick={addToCart}
                    />
                </td>
            }

        </tr>
    );
}

export default OverviewItem;