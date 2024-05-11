import React, { useState } from "react";
import Button from "../../../../../common/component/input/Button";
import InputField from "../../../../../common/component/input/InputField";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Endpoints from "../../../../../common/js/dao/dao";
import Utils from "../../../../../common/js/Utils";
import NotificationService from "../../../../../common/js/notification/NotificationService";

const StockCategory = ({ localizationHandler, category, setCategories, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);

    const [modifiedName, setModifiedName] = useState(category.name);
    const [modifiedMeasurement, setModifiedMeasurement] = useState(category.measurement);

    const getName = () => {
        if (editingEnabled) {
            return <InputField
                className="villany-atesz-stock-category-name-input"
                value={modifiedName}
                onchangeCallback={setModifiedName}
            />
        } else {
            return category.name;
        }
    }

    const getMeasurement = () => {
        if (editingEnabled) {
            return <InputField
                className="villany-atesz-stock-category-measurement-input"
                value={modifiedMeasurement}
                onchangeCallback={setModifiedMeasurement}
            />
        } else {
            return category.measurement;
        }
    }

    const openDeleteConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-stock-category-deletion",
            localizationHandler.get("confirm-category-deletion-title"),
            localizationHandler.get("confirm-category-deletion-content", category),
            [
                <Button
                    key="delete"
                    id="villany-atesz-stock-category-deletion-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteCategory}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-stock-category-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const save = async () => {
        if(Utils.isBlank(modifiedName)){
            NotificationService.showError(localizationHandler.get("blank-name"));
            return;
        }

        const response = await Endpoints.VILLANY_ATESZ_EDIT_STOCK_CATEGORY.createRequest({ name: modifiedName, measurement: modifiedMeasurement }, { stockCategoryId: category.stockCategoryId })
            .send();

        setEditingEnabled(false);
        setCategories(response);
    }

    const deleteCategory = async () => {
        const response = await Endpoints.VILLANY_ATESZ_DELETE_STOCK_CATEGORY.createRequest(null, { stockCategoryId: category.stockCategoryId })
            .send();

        setCategories(response);
        setConfirmationDialogData(null);
    }

    return (
        <tr className="villany-atesz-stock-category">
            <td className="villany-atesz-stock-category-name">{getName()}</td>
            <td className="villany-atesz-stock-category-measurement">{getMeasurement()}</td>
            <td>
                <Button
                    className="villany-atesz-stock-category-delete-button"
                    label={localizationHandler.get("delete")}
                    onclick={openDeleteConfirmation}
                />

                {!editingEnabled &&
                    <Button
                        className="villany-atesz-stock-category-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {editingEnabled &&
                    <Button
                        className="villany-atesz-stock-category-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => setEditingEnabled(false)}
                    />
                }

                {editingEnabled &&
                    <Button
                        className="villany-atesz-stock-category-save-button"
                        label={localizationHandler.get("save")}
                        onclick={save}
                    />
                }
            </td>
        </tr>
    );
}

export default StockCategory;