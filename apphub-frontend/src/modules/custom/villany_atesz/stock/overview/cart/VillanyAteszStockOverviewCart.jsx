import React, { useEffect, useState } from "react";
import localizationData from "./villany_atesz_stock_overview_cart_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_overview_cart.css";
import Utils from "../../../../../../common/js/Utils";
import useLoader from "../../../../../../common/hook/Loader";
import Endpoints from "../../../../../../common/js/dao/dao";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import Stream from "../../../../../../common/js/collection/Stream";

const VillanyAteszStockOverviewCart = ({activeCart, setActiveCart }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [carts, setCarts] = useState([]);
    const [cart, setCart] = useState(null);

    useLoader(Endpoints.VILLANY_ATESZ_GET_CARTS.createRequest(), setCarts);
    useLoader(Endpoints.VILLANY_ATESZ_GET_CART.createRequest(null, { cartId: activeCart }), setCart, [activeCart], () => !Utils.isBlank(activeCart))

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
            .map(item => <div>{item.amount + " x " + item.name}</div>)
            .toList();
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