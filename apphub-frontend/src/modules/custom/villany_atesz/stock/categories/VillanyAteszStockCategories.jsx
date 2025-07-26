import React, { useState } from "react";
import InputField from "../../../../../common/component/input/InputField";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import localizationData from "./villany_atesz_stock_categories_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_stock_categories.css";
import Button from "../../../../../common/component/input/Button";
import useLoader from "../../../../../common/hook/Loader";
import Stream from "../../../../../common/js/collection/Stream";
import StockCategory from "./StockCategory";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import { isBlank } from "../../../../../common/js/Utils";
import { VILLANY_ATESZ_CREATE_STOCK_CATEGORY, VILLANY_ATESZ_GET_STOCK_CATEGORIES } from "../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const VillanyAteszStockCategories = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [newCategoryName, setNewCategoryName] = useState("");
    const [newCategoryMeasurement, setNewCategoryMeasurement] = useState("");

    const [categories, setCategories] = useState([]);

    useLoader({ request: VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(), mapper: setCategories });

    const create = async () => {
        if (isBlank(newCategoryName)) {
            NotificationService.showError(localizationHandler.get("blank-name"));
            return;
        }

        const response = await VILLANY_ATESZ_CREATE_STOCK_CATEGORY.createRequest({ name: newCategoryName, measurement: newCategoryMeasurement })
            .send()

        setNewCategoryName("");
        setNewCategoryMeasurement("");
        setCategories(response);
    }

    const getCategories = () => {
        return new Stream(categories)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(category => <StockCategory
                key={category.stockCategoryId}
                localizationHandler={localizationHandler}
                category={category}
                setConfirmationDialogData={setConfirmationDialogData}
                setCategories={setCategories}
            />)
            .toList();
    }

    return (
        <div>
            <div id="villany-atesz-stock-new-category">
                <PreLabeledInputField
                    label={localizationHandler.get("category-name")}
                    input={<InputField
                        id="villany-atesz-stock-new-category-name"
                        placeholder={localizationHandler.get("category-name")}
                        value={newCategoryName}
                        onchangeCallback={setNewCategoryName}
                    />}
                />

                <PreLabeledInputField
                    label={localizationHandler.get("measurement")}
                    input={<InputField
                        id="villany-atesz-stock-new-category-measurement"
                        placeholder={localizationHandler.get("measurement")}
                        value={newCategoryMeasurement}
                        onchangeCallback={setNewCategoryMeasurement}
                    />}
                />

                <Button
                    id="villany-atesz-stock-new-category-button"
                    label={localizationHandler.get("create")}
                    onclick={create}
                />
            </div>

            <div id="villany-atesz-stock-categories">
                <table id="villany-atesz-stock-categories-table" className="formatted-table">
                    <thead>
                        <tr>
                            <th>{localizationHandler.get("category-name")}</th>
                            <th>{localizationHandler.get("measurement")}</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {getCategories()}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default VillanyAteszStockCategories;