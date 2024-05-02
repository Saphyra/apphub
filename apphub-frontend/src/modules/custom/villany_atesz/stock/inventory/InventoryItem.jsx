import React from "react";
import ScheduledInputField from "./ScheduledInputField";
import Utils from "../../../../../common/js/Utils";
import Endpoints from "../../../../../common/js/dao/dao";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../common/js/collection/MapStream";
import InputField from "../../../../../common/component/input/InputField";

const InventoryItem = ({ localizationHandler, item, items, categories, setItems }) => {
    const updateProperty = (property, newValue) => {
        item[property] = newValue;

        Utils.copyAndSet(items, setItems);
    }

    const sendRequest = async (endpoint, newValue) => {
        await endpoint.createRequest({ value: newValue }, { stockItemId: item.stockItemId })
            .send();
    }

    const updateCategory = async (newCategory) => {
        updateProperty("stockCategoryId", newCategory);

        sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY, newCategory);
    }

    const updateInventoried = async (inventoried) => {
        updateProperty("inventoried", inventoried);

        sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED, inventoried);
    }

    const getCategoryOptions = () => {
        return new MapStream(categories)
            .sorted((a, b) => a.value.localeCompare(b.value))
            .map((stockCategoryId, name) => new SelectOption(name, stockCategoryId))
            .toList();
    }

    return (
        <tr className="villany-atesz-stock-inventory-item">
            <td>
                <InputField
                    type="checkbox"
                    className="villany-atesz-stock-inventory-item-inventoried"
                    checked={item.inventoried}
                    onchangeCallback={updateInventoried}
                />
            </td>
            <td>
                <SelectInput
                    className="villany-atesz-stock-inventory-item-category"
                    value={item.stockCategoryId}
                    options={getCategoryOptions()}
                    onchangeCallback={updateCategory}
                />
            </td>
            <td>
                <ScheduledInputField
                    type="text"
                    className="villany-atesz-stock-inventory-item-name"
                    placeholder={localizationHandler.get("name")}
                    value={item.name}
                    onchangeCallback={(newValue) => updateProperty("name", newValue)}
                    scheduledCallback={(newValue) => sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME, newValue)}
                />
            </td>
            <td>
                <ScheduledInputField
                    type="text"
                    className="villany-atesz-stock-inventory-item-serial-number"
                    placeholder={localizationHandler.get("serial-number")}
                    value={item.serialNumber}
                    onchangeCallback={(newValue) => updateProperty("serialNumber", newValue)}
                    scheduledCallback={(newValue) => sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER, newValue)}
                />
            </td>
            <td>
                <ScheduledInputField
                    type="number"
                    className="villany-atesz-stock-inventory-item-in-car"
                    placeholder={localizationHandler.get("in-car")}
                    value={item.inCar}
                    onchangeCallback={(newValue) => updateProperty("inCar", newValue)}
                    scheduledCallback={(newValue) => sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR, newValue)}
                    disabled={item.inCart}
                />
            </td>
            <td>
                <ScheduledInputField
                    type="number"
                    className="villany-atesz-stock-inventory-item-in-storage"
                    placeholder={localizationHandler.get("in-storage")}
                    value={item.inStorage}
                    onchangeCallback={(newValue) => updateProperty("inStorage", newValue)}
                    scheduledCallback={(newValue) => sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE, newValue)}
                />
            </td>
        </tr>
    );
}

export default InventoryItem;