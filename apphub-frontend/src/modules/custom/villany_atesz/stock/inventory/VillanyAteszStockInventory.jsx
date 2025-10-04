import React, { useState } from "react";
import useLoader from "../../../../../common/hook/Loader";
import InputField from "../../../../../common/component/input/InputField";
import localizationData from "./villany_atesz_stock_inventory_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_inventory.css";
import InventoryItem from "./InventoryItem";
import Stream from "../../../../../common/js/collection/Stream";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { isBlank } from "../../../../../common/js/Utils";
import { VILLANY_ATESZ_GET_STOCK_CATEGORIES, VILLANY_ATESZ_RESET_INVENTORIED, VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS } from "../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";
import { GET_USER_SETTINGS, SET_USER_SETTINGS } from "../../../../../common/js/dao/endpoints/UserEndpoints";
import Constants from "../../../../../common/js/Constants";

const VillanyAteszStockInventory = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [search, setSearch] = useState("");
    const [items, setItems] = useState([]);
    const [categories, setCategories] = useState({});
    const [lastInventoriedCar, setLastInventoriedCar] = useState("");
    const [lastInventoriedStorage, setLastInventoriedStorage] = useState("");

    useLoader({ request: VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS.createRequest(), mapper: setItems });
    useLoader({ request: VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(), mapper: (c) => mapCategories(c) });
    useLoader({
        request: GET_USER_SETTINGS.createRequest(null, { category: Constants.SETTINGS_CATEGORY_VILLANY_ATESZ }),
        mapper: (s) => {
            setLastInventoriedCar(s[Constants.SETTINGS_KEY_STOCK_LAST_INVENTORIED_CAR])
            setLastInventoriedStorage(s[Constants.SETTINGS_KEY_STOCK_LAST_INVENTORIED_STORAGE])
        }
    });

    const updateLastInventoried = async (key, newValue, callback) => {
        const payload = {
            category: Constants.SETTINGS_CATEGORY_VILLANY_ATESZ,
            key: key,
            value: newValue
        }

        await SET_USER_SETTINGS.createRequest(payload)
            .send();

        callback(newValue);
    }

    const mapCategories = (c) => {
        const mapped = new Stream(c)
            .toMapStream(c => c.stockCategoryId, c => c.name)
            .toObject();

        setCategories(mapped);
    }

    const getItems = () => {
        return new Stream(items)
            .filter(item => {
                return isBlank(search) ||
                    new Stream([categories[item.stockCategoryId], item.name, item.serialNumber, item.inCar, item.inStorage, item.barCode])
                        .join("")
                        .toLowerCase()
                        .indexOf(search.toLocaleLowerCase()) > -1;
            })
            .sorted((a, b) => {
                const r = (categories[a.stockCategoryId]).localeCompare(categories[b.stockCategoryId]);

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

    const openResetInventoriedConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-inventory-reset-inventoried-confirmation",
            localizationHandler.get("reset-inventoried-title"),
            localizationHandler.get("reset-inventoried-content"),
            [
                <Button
                    key="reset"
                    id="villany-atesz-stock-inventory-reset-inventoried-confirm-button"
                    label={localizationHandler.get("reset-inventoried")}
                    onclick={resetInventoried}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-inventory-reset-inventoried-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const resetInventoried = async () => {
        const response = await VILLANY_ATESZ_RESET_INVENTORIED.createRequest()
            .send();

        setItems(response);

        setConfirmationDialogData(null);
    }

    return (
        <div id="villany-atesz-stock-inventory">
            <div id="villany-atesz-stock-inventory-header">
                <InputField
                    id="villany-atesz-stock-inventory-items-search"
                    placeholder={localizationHandler.get("search")}
                    value={search}
                    onchangeCallback={setSearch}
                />

                <label>
                    <span>{localizationHandler.get("car")}</span>
                    <InputField
                        id="villany-atesz-stock-inventory-last-inventoried-car"
                        type="date"
                        value={lastInventoriedCar}
                        title={localizationHandler.get("last-inventoried")}
                        onchangeCallback={newValue => updateLastInventoried(Constants.SETTINGS_KEY_STOCK_LAST_INVENTORIED_CAR, newValue, setLastInventoriedCar)}
                    />
                </label>

                <label>
                    <span>{localizationHandler.get("storage")}</span>
                    <InputField
                        id="villany-atesz-stock-inventory-last-inventoried-storage"
                        type="date"
                        value={lastInventoriedStorage}
                        title={localizationHandler.get("last-inventoried")}
                        onchangeCallback={newValue => updateLastInventoried(Constants.SETTINGS_KEY_STOCK_LAST_INVENTORIED_STORAGE, newValue, setLastInventoriedStorage)}
                    />
                </label>
            </div>

            <table id="villany-atesz-stock-inventory-items-table" className="formatted-table">
                <thead>
                    <tr>
                        <th>
                            <Button
                                id="villany-atesz-stock-inventory-reset-inventoried"
                                label="X"
                                title={localizationHandler.get("reset-inventoried")}
                                onclick={openResetInventoriedConfirmation}
                            />
                        </th>
                        <th>{localizationHandler.get("category")}</th>
                        <th>{localizationHandler.get("name")}</th>
                        <th>{localizationHandler.get("serial-number")}</th>
                        <th>{localizationHandler.get("bar-code")}</th>
                        <th>{localizationHandler.get("marked-for-acquisition")}</th>
                        <th>{localizationHandler.get("in-car")}</th>
                        <th>{localizationHandler.get("in-storage")}</th>
                        <th></th>
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