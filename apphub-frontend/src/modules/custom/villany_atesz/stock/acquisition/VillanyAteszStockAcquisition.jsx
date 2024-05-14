import React, { useState } from "react";
import Button from "../../../../../common/component/input/Button";
import loclaizationData from "./villany_atesz_stock_acquisition_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_acquisition.css";
import Utils from "../../../../../common/js/Utils";
import AcquiredItemData from "./AcquiredItemData";
import Stream from "../../../../../common/js/collection/Stream";
import AcquiredItem from "./AcquiredItem";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Endpoints from "../../../../../common/js/dao/dao";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import { validateAcquiredItems } from "../../validation/VillanyAteszValidation";

const VillanyAteszStockAcquisition = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(loclaizationData);

    const [items, setItems] = useState(Utils.hasValue(sessionStorage.stockItems) ? JSON.parse(sessionStorage.stockItems) : []);

    const updateItems = (newItems) => {
        sessionStorage.stockItems = JSON.stringify(newItems);

        setItems(newItems);
    }

    const openAddToStockConfirmation = () => {
        const validationResult = validateAcquiredItems(items);
        if (!validationResult.valid) {
            NotificationService.showError(validationResult.message);
            return;
        }

        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-acquisition-confirmation",
            localizationHandler.get("add-to-stock-confirmation-title"),
            localizationHandler.get("add-to-stock-confirmation-content"),
            [
                <Button
                    key="confirm"
                    id="villany-atesz-stock-acquisition-confirm-button"
                    label={localizationHandler.get("confirm")}
                    onclick={addToStock}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-acquisition-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const addToStock = async () => {
        await Endpoints.VILLANY_ATESZ_STOCK_ACQUIRE.createRequest(items)
            .send();

        updateItems([]);
        setConfirmationDialogData(null);

        NotificationService.showSuccess(localizationHandler.get("stored"))
    }

    const getItems = () => {
        return new Stream(items)
            .map(item => <AcquiredItem
                key={item.id}
                localizationHandler={localizationHandler}
                items={items}
                setItems={updateItems}
                item={item}
            />)
            .toList();
    }

    return (
        <div id="villany-atesz-stock-acquisition">
            <div id="villany-atesz-stock-acquisition-buttons">
                <Button
                    id="villany-atesz-stock-acquisition-new-item-button"
                    label={localizationHandler.get("new-item")}
                    onclick={() => Utils.addAndSet(items, new AcquiredItemData(), updateItems)}
                />

                <Button
                    id="villany-atesz-stock-acquisition-add-to-stock-button"
                    label={localizationHandler.get("add-to-stock")}
                    onclick={openAddToStockConfirmation}
                />

                <Button
                    id="villany-atesz-stock-acquisition-reset-button"
                    label={localizationHandler.get("reset")}
                    onclick={() => updateItems([])}
                />
            </div>

            <div id="villany-atesz-stock-acquisition-items">
                {getItems()}
            </div>
        </div>
    );
}

export default VillanyAteszStockAcquisition;