import React from "react";
import ScheduledInputField from "./ScheduledInputField";
import Utils from "../../../../../common/js/Utils";
import Endpoints from "../../../../../common/js/dao/dao";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../common/js/collection/MapStream";
import InputField from "../../../../../common/component/input/InputField";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const InventoryItem = ({ localizationHandler, item, items, categories, setItems, setConfirmationDialogData }) => {
    const updateProperty = (property, newValue) => {
        item[property] = newValue;

        Utils.copyAndSet(items, setItems);
    }

    const sendRequest = (endpoint, newValue, validation = () => true) => {
        if (!validation(newValue)) {
            return false;
        }

        const send = async () => {
            await endpoint.createRequest({ value: newValue }, { stockItemId: item.stockItemId })
                .send();
        }

        send();

        return true;
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

    const openDeletionConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-inventory-item-deletion-confirmation",
            localizationHandler.get("confirm-deletion-title"),
            localizationHandler.get("confirm-deletion-content", { name: item.name }),
            [
                <Button
                    key="delete"
                    id="villany-atesz-stock-inventory-item-confirm-delete-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteItem}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-inventory-item-cancel-delete-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteItem = async () => {
        const response = await Endpoints.VILLANY_ATESZ_DELETE_STOCK_ITEM.createRequest(null, { stockItemId: item.stockItemId })
            .send();

        setItems(response);

        setConfirmationDialogData(null);
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
                    scheduledCallback={(newValue) => sendRequest(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME, newValue, nw => !Utils.isBlank(nw))}
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
            <td>
                <Button
                    className="villany-atesz-stock-inventory-item-delete-button"
                    label={localizationHandler.get("delete")}
                    onclick={openDeletionConfirmation}
                />
            </td>
        </tr>
    );
}

export default InventoryItem;