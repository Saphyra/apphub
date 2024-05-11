import React, { useState } from "react";
import useLoader from "../../../../../common/hook/Loader";
import Endpoints from "../../../../../common/js/dao/dao";
import InputField from "../../../../../common/component/input/InputField";
import localizationData from "./villany_atesz_stock_inventory_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_inventory.css";
import Utils from "../../../../../common/js/Utils";
import InventoryItem from "./InventoryItem";
import Stream from "../../../../../common/js/collection/Stream";

const VillanyAteszStockInventory = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [search, setSearch] = useState("");
    const [items, setItems] = useState([]);
    const [categories, setCategories] = useState({});

    useLoader(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS.createRequest(), setItems);
    useLoader(Endpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(), (c) => mapCategories(c));

    const mapCategories = (c) => {
        const mapped = new Stream(c)
            .toMapStream(c => c.stockCategoryId, c => c.name)
            .toObject();

        setCategories(mapped);
    }

    const getItems = () => {
        return new Stream(items)
            .filter(item => {
                return Utils.isBlank(search) ||
                    new Stream([categories[item.stockCategoryId], item.name, item.serialNumber, item.inCar, item.inStorage])
                        .join("")
                        .toLowerCase()
                        .indexOf(search.toLocaleLowerCase()) > -1;
            })
            .sorted((a, b) => {
                const r = (categories[a] || "").localeCompare(categories[b]);

                if (r === 0) {
                    return a.name.localeCompare(b.name);
                }

                return r;
            })
            .map(item => <InventoryItem
                key={item.stockItemId}
                item={item}
                localizationHandler={localizationHandler}
                categories={categories}
                setItems={setItems}
                items={items}
                setConfirmationDialogData={setConfirmationDialogData}
            />)
            .toList();
    }

    return (
        <div id="villany-atesz-stock-inventory">
            <InputField
                id="villany-atesz-stock-inventory-items-search"
                placeholder={localizationHandler.get("search")}
                value={search}
                onchangeCallback={setSearch}
            />

            <table id="villany-atesz-stock-inventory-items-table" className="formatted-table">
                <thead>
                    <tr>
                        <th></th>
                        <th>{localizationHandler.get("category")}</th>
                        <th>{localizationHandler.get("name")}</th>
                        <th>{localizationHandler.get("serial-number")}</th>
                        <th>{localizationHandler.get("in-car")}</th>
                        <th>{localizationHandler.get("in-storage")}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {getItems()}
                </tbody>
            </table>
        </div>
    );
}

export default VillanyAteszStockInventory;