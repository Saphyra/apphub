import SelectInput, { SelectOption } from "../../../../common/component/input/SelectInput";
import MapStream from "../../../../common/js/collection/MapStream";
import { Order, OrderBy } from "./Order";

const SortSelector = ({ orderBy, setOrderBy, order, setOrder, localizationHandler }) => {
    return (
        <div id="memory-monitoring-sort-selector">
            <span>{localizationHandler.get("order-by")}</span>
            <SelectInput
                id="memory-monitoring-sort-selector-order-by"
                value={orderBy}
                onchangeCallback={setOrderBy}
                options={getOrderByOptions()}
            />

            <SelectInput
                id="memory-monitoring-sort-selector-order"
                value={order}
                onchangeCallback={setOrder}
                options={getOrderOptions()}
            />
        </div>
    );

    function getOrderByOptions() {
        return new MapStream(OrderBy)
            .toListStream(key => key)
            .map(orderBy => new SelectOption(localizationHandler.get(orderBy), orderBy))
            .toList();
    }

    function getOrderOptions(){
        return new MapStream(Order)
            .toListStream(key => key)
            .map(order => new SelectOption(localizationHandler.get(order), order))
            .toList();
    }
}

export default SortSelector;