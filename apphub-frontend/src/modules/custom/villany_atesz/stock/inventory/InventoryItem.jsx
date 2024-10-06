import React, { useState } from "react";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../common/js/collection/MapStream";
import InputField from "../../../../../common/component/input/InputField";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { validateOverviewAmount } from "../../validation/VillanyAteszValidation";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import NumberInput from "../../../../../common/component/input/NumberInput";
import ScheduledInputField from "../../../../../common/component/input/ScheduledInputField";
import { copyAndSet, isBlank } from "../../../../../common/js/Utils";
import {
    VILLANY_ATESZ_DELETE_STOCK_ITEM,
    VILLANY_ATESZ_MOVE_STOCK_TO_CAR,
    VILLANY_ATESZ_MOVE_STOCK_TO_STORAGE,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_BAR_CODE,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_MARKED_FOR_ACQUISITION,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME,
    VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER
} from "../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const InventoryItem = ({ localizationHandler, item, items, categories, setItems, setConfirmationDialogData }) => {
    const [amount, setAmount] = useState(0);

    const updateProperty = (property, newValue) => {
        item[property] = newValue;

        copyAndSet(items, setItems);
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

        sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY, newCategory);
    }

    const updateInventoried = async (inventoried) => {
        updateProperty("inventoried", inventoried);

        sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED, inventoried);
    }

    const updateMarkedForAcquisition = async (markedForAcquisition) => {
        updateProperty("markedForAcquisition", markedForAcquisition);

        sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_MARKED_FOR_ACQUISITION, markedForAcquisition);
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
        const response = await VILLANY_ATESZ_DELETE_STOCK_ITEM.createRequest(null, { stockItemId: item.stockItemId })
            .send();

        setItems(response);

        setConfirmationDialogData(null);
    }

    const moveToCar = async () => {
        const validationResult = validateOverviewAmount(amount);
        if (!validationResult.valid) {
            NotificationService.showError(validationResult.message);
            return;
        }

        const response = await VILLANY_ATESZ_MOVE_STOCK_TO_CAR.createRequest({ value: amount }, { stockItemId: item.stockItemId })
            .send();

        setItems(response);
        setAmount(0)
    }

    const moveToStorage = async () => {
        const validationResult = validateOverviewAmount(amount);
        if (!validationResult.valid) {
            NotificationService.showError(validationResult.message);
            return;
        }

        const response = await VILLANY_ATESZ_MOVE_STOCK_TO_STORAGE.createRequest({ value: amount }, { stockItemId: item.stockItemId })
            .send();

        setItems(response);
        setAmount(0)
    }

    const getCategoryName = () => {
        return new MapStream(categories)
            .filter(stockCategoryId => stockCategoryId == item.stockCategoryId)
            .toListStream()
            .findFirst()
            .orElse("");
    }

    if (item.inventoried) {
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
                    <span className="villany-atesz-stock-inventory-item-category selectable">{getCategoryName()}</span>
                </td>
                <td>
                    <span className="villany-atesz-stock-inventory-item-name selectable">{item.name}</span>
                </td>
                <td>
                    <span className="villany-atesz-stock-inventory-item-serial-number selectable">{item.serialNumber}</span>
                </td>
                <td>
                    <span className="villany-atesz-stock-inventory-item-bar-code selectable">{item.barCode}</span>
                </td>
                <td>
                    <InputField
                        type="checkbox"
                        className="villany-atesz-stock-inventory-item-marked-for-acquisition"
                        checked={item.markedForAcquisition}
                        onchangeCallback={updateMarkedForAcquisition}
                    />
                </td>
                <td>
                    <span className="villany-atesz-stock-inventory-item-in-car selectable">{item.inCar}</span>
                </td>
                <td>
                    <span className="villany-atesz-stock-inventory-item-in-storage selectable">{item.inStorage}</span>
                </td>
                <td>
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
    } else {
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
                        scheduledCallback={(newValue) => sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME, newValue, nw => !isBlank(nw))}
                        style={{ width: 8 * item.name.length + "px" }}
                    />
                </td>
                <td>
                    <ScheduledInputField
                        type="text"
                        className="villany-atesz-stock-inventory-item-serial-number"
                        placeholder={localizationHandler.get("serial-number")}
                        value={item.serialNumber}
                        onchangeCallback={(newValue) => updateProperty("serialNumber", newValue)}
                        scheduledCallback={(newValue) => sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER, newValue)}
                    />
                </td>
                <td>
                    <ScheduledInputField
                        type="text"
                        className="villany-atesz-stock-inventory-item-bar-code"
                        placeholder={localizationHandler.get("bar-code")}
                        value={item.barCode}
                        onchangeCallback={(newValue) => updateProperty("barCode", newValue)}
                        scheduledCallback={(newValue) => sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_BAR_CODE, newValue)}
                    />
                </td>
                <td>
                    <InputField
                        type="checkbox"
                        className="villany-atesz-stock-inventory-item-marked-for-acquisition"
                        checked={item.markedForAcquisition}
                        onchangeCallback={updateMarkedForAcquisition}
                    />
                </td>
                <td>
                    <ScheduledInputField
                        type="number"
                        className="villany-atesz-stock-inventory-item-in-car"
                        placeholder={localizationHandler.get("in-car")}
                        value={item.inCar}
                        onchangeCallback={(newValue) => updateProperty("inCar", newValue)}
                        scheduledCallback={(newValue) => sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR, newValue)}
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
                        scheduledCallback={(newValue) => sendRequest(VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE, newValue)}
                    />
                </td>
                <td>
                    <NumberInput
                        className="villany-atesz-stock-inventory-item-amount"
                        placeholder={localizationHandler.get("amount")}
                        value={amount}
                        onchangeCallback={setAmount}
                    />

                    <Button
                        className="villany-atesz-stock-inventory-item-move-to-car-button"
                        label={localizationHandler.get("move-to-car")}
                        onclick={moveToCar}
                    />

                    <Button
                        className="villany-atesz-stock-inventory-item-move-to-storage-button"
                        label={localizationHandler.get("move-to-storage")}
                        onclick={moveToStorage}
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
}

export default InventoryItem;