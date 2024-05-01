import React, { useEffect, useState } from "react";
import localizationData from "./villany_atesz_stock_overview_cart_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_overview_cart.css";
import Utils from "../../../../../../common/js/Utils";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import Stream from "../../../../../../common/js/collection/Stream";
import Button from "../../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Endpoints from "../../../../../../common/js/dao/dao";

const VillanyAteszStockOverviewCart = ({ activeCart, setActiveCart, cart, carts, setCarts, setCart, setItems, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);


    const getAvailableCartOptions = () => {
        return new Stream(carts)
            .map(cart => new SelectOption(cart.contact.code + " - " + cart.contact.name, cart.cartId))
            .sorted((a, b) => a.label.localeCompare(b.label))
            .reverse()
            .add(new SelectOption(localizationHandler.get("choose-cart"), ""))
            .reverse()
            .toList();
    }

    const getItems = () => {
        return new Stream(cart.items)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(item => <div key={item.stockItemId}>{item.amount + " x " + item.name}</div>)
            .toList();
    }

    const openFinalizeCartConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-finalize-cart-confirmation",
            localizationHandler.get("confirm-finalize-cart-title"),
            localizationHandler.get("confirm-finalize-cart-content", { code: cart.contact.code, name: cart.contact.name, totalPrice: cart.totalPrice }),
            [
                <Button
                    key="finalize"
                    id="villany-atesz-stock-finalize-confirm-button"
                    label={localizationHandler.get("finalize")}
                    onclick={finalizeCart}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-finalize-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const openDeleteCartConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-delete-cart-confirmation",
            localizationHandler.get("confirm-delete-cart-title"),
            localizationHandler.get("confirm-delete-cart-content", { code: cart.contact.code, name: cart.contact.name, totalPrice: cart.totalPrice }),
            [
                <Button
                    key="delete"
                    id="villany-atesz-stock-delete-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteCart}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-delete-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const finalizeCart = async () => {
        const response = await Endpoints.VILLANY_ATESZ_FINALIZE_CART.createRequest(null, { cartId: cart.cartId })
            .send();

        setCarts(response.carts);
        setItems(response.items);
        setCart(null);
        setActiveCart("");

        setConfirmationDialogData(null);
    }

    const deleteCart = async () => {
        const response = await Endpoints.VILLANY_ATESZ_DELETE_CART.createRequest(null, { cartId: cart.cartId })
            .send();

        setCarts(response.carts);
        setItems(response.items);
        setCart(null);
        setActiveCart("");

        setConfirmationDialogData(null);
    }

    return (
        <div id="villany-atesz-stock-overview-cart">
            <PreLabeledInputField
                id="villany-atesz-stock-overview-cart-selector-label"
                label={localizationHandler.get("active-cart")}
                input={<SelectInput
                    id="villany-atesz-stock-overview-cart-selector"
                    value={activeCart}
                    onchangeCallback={setActiveCart}
                    options={getAvailableCartOptions()}
                />}
            />

            {Utils.hasValue(cart) &&
                <div id="villany-atesz-stock-overview-cart-details">
                    <fieldset>
                        <legend>{localizationHandler.get("operations")}</legend>

                        <Button
                            id="villany-atesz-stock-overview-finalize-cart"
                            label={localizationHandler.get("finalize")}
                            onclick={openFinalizeCartConfirmation}
                        />

                        <Button
                            id="villany-atesz-stock-overview-finalize-cart"
                            label={localizationHandler.get("delete")}
                            onclick={openDeleteCartConfirmation}
                        />
                    </fieldset>

                    <fieldset>
                        <legend>{localizationHandler.get("contact-info")}</legend>

                        <div>
                            <span>{localizationHandler.get("code")}</span>
                            <span>: </span>
                            <span id="villany-atesz-stock-overview-cart-details-contact-code">{cart.contact.code}</span>
                        </div>

                        <div>
                            <span>{localizationHandler.get("name")}</span>
                            <span>: </span>
                            <span id="villany-atesz-stock-overview-cart-details-contact-name">{cart.contact.name}</span>
                        </div>

                        <div>
                            <span>{localizationHandler.get("address")}</span>
                            <span>: </span>
                            <span id="villany-atesz-stock-overview-cart-details-contact-address">{cart.contact.address}</span>
                        </div>

                        <div>
                            <span>{localizationHandler.get("phone")}</span>
                            <span>: </span>
                            <span id="villany-atesz-stock-overview-cart-details-contact-phone">{cart.contact.phone}</span>
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend>{localizationHandler.get("total-price")}</legend>

                        <div>
                            <span id="villany-atesz-stock-overview-cart-details-total-price">{cart.totalPrice}</span>
                            <span> Ft</span>
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend>{localizationHandler.get("items")}</legend>

                        <div id="villany-atesz-stock-overview-cart-items">{getItems()}</div>
                    </fieldset>
                </div>
            }
        </div>
    );
}

export default VillanyAteszStockOverviewCart;