import React, { useState } from "react";
import Button from "../../../../../common/component/input/Button";
import loclaizationData from "./villany_atesz_stock_acquisition_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_acquisition.css";
import AcquiredItemData from "./AcquiredItemData";
import Stream from "../../../../../common/js/collection/Stream";
import AcquiredItem from "./AcquiredItem";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import { validateAcquiredItems } from "../../validation/VillanyAteszValidation";
import useCache from "../../../../../common/hook/Cache";
import LocalDate from "../../../../../common/js/date/LocalDate";
import InputField from "../../../../../common/component/input/InputField";
import VillanyAteszStockAcquisitionHistory from "./history/VillanyAteszStockAcquisitionHistory";
import { addAndSet, hasValue } from "../../../../../common/js/Utils";
import { VILLANY_ATESZ_GET_STOCK_CATEGORIES, VILLANY_ATESZ_STOCK_ACQUIRE } from "../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const VillanyAteszStockAcquisition = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(loclaizationData);

    const [items, setItems] = useState(hasValue(sessionStorage.stockItems) ? JSON.parse(sessionStorage.stockItems) : []);
    const [acquiredAt, setAcquiredAt] = useState(LocalDate.now().toString());
    const [displayHistory, setDisplayHistory] = useState(false);

    const refetchCategories = useCache(
        "stock-categories",
        VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(),
        (categories) => { }
    );

    const updateItems = (newItems) => {
        sessionStorage.stockItems = JSON.stringify(newItems);

        setItems(newItems);
    }

    const reset = () => {
        updateItems([]);

        refetchCategories();
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
        const payload = {
            items: items,
            acquiredAt: acquiredAt
        }

        await VILLANY_ATESZ_STOCK_ACQUIRE.createRequest(payload)
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
                    onclick={() => addAndSet(items, new AcquiredItemData(), updateItems)}
                />

                <div>
                    <InputField
                        id="villany-atesz-stock-acquisition-acquired-at"
                        type="date"
                        value={acquiredAt}
                        onchangeCallback={setAcquiredAt}
                    />

                    <Button
                        id="villany-atesz-stock-acquisition-add-to-stock-button"
                        label={localizationHandler.get("add-to-stock")}
                        onclick={openAddToStockConfirmation}
                    />
                </div>

                <Button
                    id="villany-atesz-stock-acquisition-reset-button"
                    label={localizationHandler.get("reset")}
                    onclick={reset}
                />

                {displayHistory &&
                    <Button
                        id="villany-atesz-stock-acquisition-hide-history-button"
                        label={localizationHandler.get("hide-history")}
                        onclick={() => setDisplayHistory(false)}
                    />
                }

                {!displayHistory &&
                    <Button
                        id="villany-atesz-stock-acquisition-show-history-button"
                        label={localizationHandler.get("show-history")}
                        onclick={() => setDisplayHistory(true)}
                    />
                }
            </div>

            <div id="villany-atesz-stock-acquisition-items">
                {!displayHistory && getItems()}
                {displayHistory && <VillanyAteszStockAcquisitionHistory/>}
            </div>
        </div>
    );
}

export default VillanyAteszStockAcquisition;