import React from "react";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import MapStream from "../../../../../../common/js/collection/MapStream";
import OrderBy from "./OrderBy";
import Order from "./Order";

const TradeCommoditiesResultOrder = ({ localizationHandler, order, orderBy, setOrder, setOrderBy }) => {
    return (
        <div id="elite-base-ct-trade-commodities-order-buttons-wrapper">
            <PreLabeledInputField
                label={localizationHandler.get("order-by")}
                input={<SelectInput
                    value={orderBy}
                    onchangeCallback={setOrderBy}
                    options={getOrderByOptions()}
                />
                }
            />

            <SelectInput
                value={order}
                onchangeCallback={setOrder}
                options={getOrderOptions()}
            />
        </div>
    );

    function getOrderByOptions() {
        return new MapStream(OrderBy)
            .toListStream()
            .map(o => new SelectOption(localizationHandler.get(o.key), o.key))
            .toList();
    }

    function getOrderOptions() {
        return new MapStream(Order)
            .toListStream()
            .map(o => new SelectOption(localizationHandler.get(o.key), o.key))
            .toList();
    }
}

export default TradeCommoditiesResultOrder;