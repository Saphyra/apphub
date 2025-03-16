import React, { useEffect, useState } from "react";
import localizationData from "./commodity_selector_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import DataListInputField, { DataListInputEntry } from "../../../../../../common/component/input/DataListInputField";
import { hasValue } from "../../../../../../common/js/Utils";
import useCache from "../../../../../../common/hook/Cache";
import { ELITE_BASE_COMMODITY_TRADING_COMMODITIES } from "../../EliteBaseEndpoints";
import Stream from "../../../../../../common/js/collection/Stream";
import "./commodity_selector.css";

const CommoditySelector = ({ commodity, setCommodity }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [selectedCommodity, setSelectedCommodity] = useState(new DataListInputEntry(commodity, hasValue(sessionStorage.eliteBaseCommoditySelectorCommodity) ? sessionStorage.eliteBaseCommoditySelectorCommodity : ""));
    const [commodities, setCommodities] = useState([]);

    useCache("elite-base-commodities", ELITE_BASE_COMMODITY_TRADING_COMMODITIES.createRequest(), result => setCommodities(new Stream(result).sorted().toList()));

    useEffect(updateCommodity, [selectedCommodity, commodities]);

    return (
        <div>
            <PreLabeledInputField
                label={localizationHandler.get("commodity") + ":"}
                input={<DataListInputField
                    className={"elite-base-commodity-selector" + (hasValue(commodity) ? " locked" : "")}
                    value={selectedCommodity}
                    setValue={setSelectedCommodity}
                    placeholder={localizationHandler.get("commodity")}
                    options={getOptions()}
                    onKeyUp={() => setCommodity(null)}
                />
                }
            />
        </div>
    );

    function getOptions() {
        return new Stream(commodities)
            .map(c => new DataListInputEntry(c, c))
            .toList()
    }

    function updateCommodity() {
        if (hasValue(selectedCommodity.key)) {
            setCommodity(selectedCommodity.key);
            sessionStorage.eliteBaseCommoditySelectorCommodity = selectedCommodity.value;
        } else if(hasValue(commodities)) {
            new Stream(commodities)
                .filter(commodity => commodity.toLowerCase() === selectedCommodity.value.toLowerCase())
                .findFirst()
                .ifPresent(commodity => {
                    setCommodity(commodity);
                    sessionStorage.eliteBaseCommoditySelectorCommodity = selectedCommodity.value;
                });
        }
    }
}

export default CommoditySelector;