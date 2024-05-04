import React, { useState } from "react";
import Endpoints from "../../../../../common/js/dao/dao";
import useCache from "../../../../../common/hook/Cache";
import Utils from "../../../../../common/js/Utils";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import Stream from "../../../../../common/js/collection/Stream";
import NumberInput from "../../../../../common/component/input/NumberInput";
import Button from "../../../../../common/component/input/Button";

const AcquiredItem = ({ item, localizationHandler, items, setItems }) => {
    const [categories, setCategories] = useState([]);
    const [stockItems, setStockItems] = useState([]);

    useCache(
        "stock-categories",
        Endpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES.createRequest(),
        setCategories
    );

    useCache(
        item.stockCategoryId,
        Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS_FOR_CATEGORY.createRequest(null, { stockCategoryId: item.stockCategoryId }),
        setStockItems,
        !Utils.isBlank(item.stockCategoryId)
    );

    const set = (property, value) => {
        item[property] = value;

        if (property === "stockCategoryId") {
            item.stockItemId = "";
        }

        Utils.copyAndSet(items, setItems);
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
        Utils.removeAndSet(items, i => i.id === item.id, setItems);
    }

    return (
        <div className="villany-atesz-stock-acquisition-item">
            <PreLabeledInputField
                label={localizationHandler.get("category")}
                input={<SelectInput
                    className={"villany-atesz-stock-acquisition-item-category"}
                    value={item.stockCategoryId}
                    options={getCategoryOptions()}
                    onchangeCallback={(value) => set("stockCategoryId", value)}
                />}
            />

            {!Utils.isBlank(item.stockCategoryId) &&
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

            {!Utils.isBlank(item.stockItemId) &&
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

            {!Utils.isBlank(item.stockItemId) &&
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

            {!Utils.isBlank(item.stockItemId) &&
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

            <Button
                className="villany-atesz-stock-acquisition-item-remove-button"
                label={localizationHandler.get("remove")}
                onclick={remove}
            />
        </div>
    );
}

export default AcquiredItem;