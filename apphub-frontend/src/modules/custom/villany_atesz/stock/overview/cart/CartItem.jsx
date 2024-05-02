import React from "react";
import Button from "../../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Endpoints from "../../../../../../common/js/dao/dao";

const CartItem = ({ cartId, item, setConfirmationDialogData, localizationHandler, setItems, setCart }) => {
    const openRemoveConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-overview-cart-item-remove-confirmation",
            localizationHandler.get("cart-item-removal-title"),
            localizationHandler.get("cart-item-removal-content", item),
            [
                <Button
                    key="remove"
                    id="villany-atesz-stock-overview-cart-item-removal-confirm-button"
                    label={localizationHandler.get("remove")}
                    onclick={removeItem}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-overview-cart-item-removal-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const removeItem = async () => {
        const response = await Endpoints.VILLANY_ATESZ_REMOVE_FROM_CART.createRequest(null, { cartId: cartId, stockItemId: item.stockItemId })
            .send();

        setCart(response.cart);
        setItems(response.items);
        setConfirmationDialogData(null);
    }

    return (
        <div className="villany-atesz-stock-overview-cart-item">
            <span>{item.amount}</span>
            <span> x </span>
            <span>{item.name}</span>
            <Button
                className="villany-atesz-stock-overview-cart-item-remove-button"
                label="X"
                onclick={openRemoveConfirmation}
            />
        </div>
    );
}

export default CartItem;