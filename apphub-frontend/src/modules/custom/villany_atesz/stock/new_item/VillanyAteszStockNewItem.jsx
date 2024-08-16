import React, { useState } from "react";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import localizationData from "./villany_atesz_stock_new_item_localization.json";
import useLoader from "../../../../../common/hook/Loader";
import Endpoints from "../../../../../common/js/dao/dao";
import "./villany_atesz_stock_new_item.css"
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";
import Button from "../../../../../common/component/input/Button";
import InputField from "../../../../../common/component/input/InputField";
import NumberInput from "../../../../../common/component/input/NumberInput";
import Utils from "../../../../../common/js/Utils";
import NotificationService from "../../../../../common/js/notification/NotificationService";

const VillanyAteszStockNewItem = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [categories, setCategories] = useState([]);

    const [stockCategoryId, setStockCategoryId] = useState("");
    const [name, setName] = useState("");
    const [serialNumber, setSerialNumber] = useState("");
    const [barCode, setBarCode] = useState("");

    const [inCar, setInCar] = useState(0);
    const [inStorage, setInStorage] = useState(0);

    const [price, setPrice] = useState(0);

    useLoader(Endpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(), setCategories);

    const getOptions = () => {
        return new Stream(categories)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(category => new SelectOption(category.name, category.stockCategoryId))
            .reverse()
            .add(new SelectOption(localizationHandler.get("choose", "")))
            .reverse()
            .toList();
    }

    const create = async () => {
        if (Utils.isBlank(stockCategoryId)) {
            NotificationService.showError(localizationHandler.get("choose-category"));
            return;
        }

        if (Utils.isBlank(name)) {
            NotificationService.showError(localizationHandler.get("blank-name"));
            return;
        }

        const payload = {
            stockCategoryId: stockCategoryId,
            name: name,
            serialNumber: serialNumber,
            barCode: barCode,
            inCar: inCar,
            inStorage: inStorage,
            price: price
        }

        await Endpoints.VILLANY_ATESZ_CREATE_STOCK_ITEM.createRequest(payload)
            .send();

        setStockCategoryId("");
        setName("");
        setSerialNumber("");
        setBarCode("");
        setInCar(0);
        setInStorage(0);
        setPrice(0);

        NotificationService.showSuccess(localizationHandler.get("item-created"));
    }

    return (
        <div id="villany-atesz-sotck-new-item">
            <div className="villany-atesz-stock-new-item-fieldset-wrapper">
                <fieldset>
                    <legend>{localizationHandler.get("basics")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("category")}
                        input={<SelectInput
                            id="villany-atesz-new-item-category"
                            value={stockCategoryId}
                            onchangeCallback={setStockCategoryId}
                            options={getOptions()}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("name")}
                        input={<InputField
                            id="villany-atesz-new-item-name"
                            placeholder={localizationHandler.get("name")}
                            value={name}
                            onchangeCallback={setName}
                            style={{
                                width: 8 * name.length + "px",
                                minWidth: "200px"
                            }}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("serial-number")}
                        input={<InputField
                            id="villany-atesz-new-item-serial-number"
                            placeholder={localizationHandler.get("serial-number")}
                            value={serialNumber}
                            onchangeCallback={setSerialNumber}
                            style={{
                                width: 8 * serialNumber.length + "px",
                                minWidth: "200px"
                            }}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("bar-code")}
                        input={<InputField
                            id="villany-atesz-new-item-bar-code"
                            placeholder={localizationHandler.get("bar-code")}
                            value={barCode}
                            onchangeCallback={setBarCode}
                            style={{
                                width: 8 * barCode.length + "px",
                                minWidth: "200px"
                            }}
                        />}
                    />
                </fieldset>
            </div>

            <div className="villany-atesz-stock-new-item-fieldset-wrapper">
                <fieldset>
                    <legend>{localizationHandler.get("stock")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("in-car")}
                        input={<NumberInput
                            id="villany-atesz-new-item-in-car"
                            placeholder={localizationHandler.get("in-car")}
                            value={inCar}
                            onchangeCallback={setInCar}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("in-storage")}
                        input={<NumberInput
                            id="villany-atesz-new-item-in-storage"
                            placeholder={localizationHandler.get("in-storage")}
                            value={inStorage}
                            onchangeCallback={setInStorage}
                        />}
                    />
                </fieldset>
            </div>

            <div className="villany-atesz-stock-new-item-fieldset-wrapper">
                <fieldset>
                    <legend>{localizationHandler.get("price")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("price")}
                        input={<NumberInput
                            id="villany-atesz-new-item-price"
                            placeholder={localizationHandler.get("price")}
                            value={price}
                            onchangeCallback={setPrice}
                        />}
                    />
                </fieldset>
            </div>

            <Button
                id="villany-atesz-stock-new-item-create-button"
                label={localizationHandler.get("create")}
                onclick={create}
            />
        </div>
    );
}

export default VillanyAteszStockNewItem;