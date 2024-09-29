import React, { useEffect, useState } from "react";
import Endpoints, { ResponseStatus } from "../../../../../common/js/dao/dao";
import useCache from "../../../../../common/hook/Cache";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";
import NumberInput from "../../../../../common/component/input/NumberInput";
import Button from "../../../../../common/component/input/Button";
import InputField from "../../../../../common/component/input/InputField";
import useFocus from "../../../../../common/hook/UseFocus";
import ErrorHandler from "../../../../../common/js/dao/ErrorHandler";
import PostLabeledInputField from "../../../../../common/component/input/PostLabeledInputField";
import { copyAndSet, isBlank, removeAndSet } from "../../../../../common/js/Utils";

const AcquiredItem = ({ item, localizationHandler, items, setItems }) => {
    const [barCode, setBarCode] = useState("");
    const [categories, setCategories] = useState([]);
    const [stockItems, setStockItems] = useState([]);
    const [inputRef, setFocus] = useFocus();
    const [timeout, sTimeout] = useState(null);

    useCache(
        "stock-categories",
        Endpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(),
        setCategories
    );

    useCache(
        item.stockCategoryId,
        Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS_FOR_CATEGORY.createRequest(null, { stockCategoryId: item.stockCategoryId }),
        setStockItems,
        !isBlank(item.stockCategoryId)
    );

    useEffect(() => setFocus(), []);
    useEffect(() => scheduleSearch(), [barCode]);
    useEffect(() => loadBarCode(), [item.stockItemId]);
    useEffect(() => setBarCode(""), [item.stockCategoryId]);

    const scheduleSearch = () => {
        if (isBlank(barCode)) {
            return;
        }

        if (timeout) {
            clearTimeout(timeout);
        }

        sTimeout(setTimeout(searchByBarCode, 500));
    }

    const searchByBarCode = async () => {
        const response = await Endpoints.VILLANY_ATESZ_FIND_STOCK_ITEM_BY_BAR_CODE.createRequest({ value: barCode })
            .addErrorHandler(new ErrorHandler(
                response => response.status == ResponseStatus.NOT_FOUND,
                () => { }
            ))
            .send();

        set("stockCategoryId", response.stockCategoryId);
        set("stockItemId", response.stockItemId);
        set("barCode", response.barCode);
    }

    const loadBarCode = () => {
        if (isBlank(item.stockItemId)) {
            return;
        }

        const fetch = async () => {
            const response = await Endpoints.VILLANY_ATESZ_FIND_BAR_CODE_BY_STOCK_ITEM_ID.createRequest(null, { stockItemId: item.stockItemId })
                .send();

            set("barCode", response.value);
        }
        fetch();
    }

    const set = (property, value) => {
        item[property] = value;

        if (property === "stockCategoryId") {
            item.stockItemId = "";
        }

        copyAndSet(items, setItems);
    }

    const getCategoryOptions = () => {
        return new Stream(categories)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(category => new SelectOption(category.name, category.stockCategoryId))
            .reverse()
            .add(new SelectOption(localizationHandler.get("choose"), ""))
            .reverse()
            .toList();
    }

    const getItemOptions = () => {
        return new Stream(stockItems)
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(category => new SelectOption(category.name, category.stockItemId))
            .reverse()
            .add(new SelectOption(localizationHandler.get("choose"), ""))
            .reverse()
            .toList();
    }

    const remove = () => {
        removeAndSet(items, i => i.id === item.id, setItems);
    }

    return (
        <div className="villany-atesz-stock-acquisition-item">
            {isBlank(item.stockCategoryId) &&
                <PreLabeledInputField
                    label={localizationHandler.get("bar-code")}
                    input={<InputField
                        className="villany-atesz-stock-acquisition-item-bar-code-search"
                        placeholder={localizationHandler.get("bar-code")}
                        inputRef={inputRef}
                        value={barCode}
                        onchangeCallback={setBarCode}
                    />}
                />
            }

            <PreLabeledInputField
                label={localizationHandler.get("category")}
                input={<SelectInput
                    className={"villany-atesz-stock-acquisition-item-category"}
                    value={item.stockCategoryId}
                    options={getCategoryOptions()}
                    onchangeCallback={(value) => set("stockCategoryId", value)}
                />}
            />

            {!isBlank(item.stockCategoryId) &&
                <PreLabeledInputField
                    label={localizationHandler.get("item")}
                    input={<SelectInput
                        className={"villany-atesz-stock-acquisition-item-item"}
                        value={item.stockItemId}
                        options={getItemOptions()}
                        onchangeCallback={(value) => set("stockItemId", value)}
                    />}
                />
            }

            {!isBlank(item.stockItemId) &&
                <PreLabeledInputField
                    label={localizationHandler.get("to-car")}
                    input={<NumberInput
                        className={"villany-atesz-stock-acquisition-item-to-car"}
                        placeholder={localizationHandler.get("to-car")}
                        value={item.inCar}
                        onchangeCallback={(value) => set("inCar", value)}
                    />}
                />
            }

            {!isBlank(item.stockItemId) &&
                <PreLabeledInputField
                    label={localizationHandler.get("to-storage")}
                    input={<NumberInput
                        className={"villany-atesz-stock-acquisition-item-to-storage"}
                        placeholder={localizationHandler.get("to-storage")}
                        value={item.inStorage}
                        onchangeCallback={(value) => set("inStorage", value)}
                    />}
                />
            }

            {!isBlank(item.stockItemId) &&
                <PreLabeledInputField
                    label={localizationHandler.get("price")}
                    input={<NumberInput
                        className={"villany-atesz-stock-acquisition-item-price"}
                        placeholder={localizationHandler.get("price")}
                        value={item.price}
                        onchangeCallback={(value) => set("price", value)}
                    />}
                />
            }

            {!isBlank(item.stockItemId) &&
                <PreLabeledInputField
                    label={localizationHandler.get("bar-code")}
                    input={<InputField
                        className={"villany-atesz-stock-acquisition-item-bar-code"}
                        placeholder={localizationHandler.get("bar-code")}
                        value={item.barCode}
                        onchangeCallback={(value) => set("barCode", value)}
                    />}
                />
            }

            {!isBlank(item.stockItemId) &&
                <PostLabeledInputField
                    label={localizationHandler.get("force-update-price")}
                    input={<InputField
                        className={"villany-atesz-stock-acquisition-item-force-update-price"}
                        type="checkbox"
                        checked={item.forceUpdatePrice}
                        onchangeCallback={(checked) => set("forceUpdatePrice", checked)}
                    />}
                />
            }

            <Button
                className="villany-atesz-stock-acquisition-item-remove-button"
                label={localizationHandler.get("remove")}
                onclick={remove}
            />
        </div>
    );
}

export default AcquiredItem;