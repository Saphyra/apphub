import React, { useEffect, useState } from "react";
import localizationData from "./villany_atesz_stock_overview_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_overview.css";
import useLoader from "../../../../../common/hook/Loader";
import Endpoints from "../../../../../common/js/dao/dao";
import InputField from "../../../../../common/component/input/InputField";
import Stream from "../../../../../common/js/collection/Stream";
import Utils from "../../../../../common/js/Utils";
import OverviewItem from "./OverviewItem";
import VillanyAteszStockOverviewCart from "./cart/VillanyAteszStockOverviewCart";
import useFocus from "../../../../../common/hook/UseFocus";

const VillanyAteszStockOverview = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [search, setSearch] = useState("");
    const [items, setItems] = useState([]);
    const [activeCart, setActiveCart] = useState(Utils.hasValue(sessionStorage.activeCart) ? sessionStorage.activeCart : "");
    const [cart, setCart] = useState(null);
    const [carts, setCarts] = useState([]);
    const [inputRef, setInputFocus] = useFocus();

    useLoader(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS.createRequest(), setItems);
    useLoader(Endpoints.VILLANY_ATESZ_GET_CART.createRequest(null, { cartId: activeCart }), setCart, [activeCart], () => !Utils.isBlank(activeCart));
    useLoader(Endpoints.VILLANY_ATESZ_GET_CARTS.createRequest(), setCarts);

    useEffect(() => focus(), [search]);

    const focus = () => {
        if (search.length > 0) {
            return;
        }

        setInputFocus();
    }

    const updateActiveCart = (cartId) => {
        sessionStorage.activeCart = cartId;
        setActiveCart(cartId);
    }

    const getItems = () => {
        return new Stream(items)
            .filter(item => item.inCar > 0 || item.inStorage > 0)
            .filter(item => {
                return Utils.isBlank(search) ||
                    new Stream([item.category.name, item.name, item.serialNumber, item.inCar, item.inCart, item.inStorage, item.price, item.barCode])
                        .join("")
                        .toLowerCase()
                        .indexOf(search.toLocaleLowerCase()) > -1;
            })
            .sorted((a, b) => {
                const r = a.category.name.localeCompare(b.category.name);

                if (r === 0) {
                    return a.name.localeCompare(b.name);
                }

                return r;
            })
            .map(item => <OverviewItem
                key={item.stockItemId}
                item={item}
                activeCart={activeCart}
                localizationHandler={localizationHandler}
                setItems={setItems}
                setCart={setCart}
                setSearch={setSearch}
            />)
            .toList();
    }

    return (
        <div id="villany-atesz-stock-overview">
            <div id="villany-atesz-stock-overview-items">
                <InputField
                    id="villany-atesz-stock-overview-items-search"
                    placeholder={localizationHandler.get("search")}
                    value={search}
                    onchangeCallback={setSearch}
                    autoFocus={true}
                    inputRef={inputRef}
                />

                <table id="villany-atesz-stock-overview-items-table" className="formatted-table">
                    <thead>
                        <tr>
                            <th>{localizationHandler.get("category")}</th>
                            <th>{localizationHandler.get("name")}</th>
                            <th>{localizationHandler.get("serial-number")}</th>
                            <th>{localizationHandler.get("in-car")}</th>
                            <th>{localizationHandler.get("in-cart")}</th>
                            <th>{localizationHandler.get("in-storage")}</th>
                            <th>{localizationHandler.get("price")}</th>
                            <th>{localizationHandler.get("stock-value")}</th>
                            {!Utils.isBlank(activeCart) &&
                                <th></th>
                            }
                        </tr>
                    </thead>
                    <tbody>
                        {getItems()}
                    </tbody>
                </table>
            </div>

            <VillanyAteszStockOverviewCart
                activeCart={activeCart}
                setActiveCart={updateActiveCart}
                cart={cart}
                carts={carts}
                setCart={setCart}
                setCarts={setCarts}
                setConfirmationDialogData={setConfirmationDialogData}
                setItems={setItems}
            />
        </div>
    );
}

export default VillanyAteszStockOverview;