import React, { useState } from "react";
import Button from "../../../../../../common/component/input/Button";
import { PAGE_SIZE } from "./TradeCommoditiesPage";
import Stream from "../../../../../../common/js/collection/Stream";
import Order from "./Order";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import OrderBy from "./OrderBy";
import MapStream from "../../../../../../common/js/collection/MapStream";
import { cacheAndUpdate, cachedOrDefault, formatNumber } from "../../../../../../common/js/Utils";
import PowerNames from "../../../common/localization/PowerNames";
import LocalDateTime from "../../../../../../common/js/date/LocalDateTime";
import Entry from "../../../../../../common/js/collection/Entry";
import TradeCommoditiesResultOrder from "./TradeCommoditiesResultOrder";

const TradeCommoditiesResult = ({ localizationHandler, searchResult, displayed, setDisplayed }) => {
    const CACHE_KEY_ORDER = "eliteBaseCommodityTradingResultOrder";
    const CACHE_KEY_ORDER_BY = "eliteBaseCommodityTradingResultOrderBy";

    const [orderBy, setOrderBy] = useState(cachedOrDefault(CACHE_KEY_ORDER_BY, OrderBy.SYSTEM_DISTANCE.key));
    const [order, setOrder] = useState(cachedOrDefault(CACHE_KEY_ORDER, Order.ASCENDING.key));

    return (
        <div>
            <TradeCommoditiesResultOrder
                localizationHandler={localizationHandler}
                order={order}
                orderBy={orderBy}
                setOrder={v => cacheAndUpdate(CACHE_KEY_ORDER, v, setOrder)}
                setOrderBy={v => cacheAndUpdate(CACHE_KEY_ORDER_BY, v, setOrderBy)}
            />

            <table id="elite-base-ct-trade-commodities-search-result-table" className="formatted-table selectable">
                <thead>
                    <tr>
                        <td
                            colSpan={12}
                            id="elite-base-ct-trade-commodities-search-result-number-of-result"
                        >
                            {localizationHandler.get("number-of-results", { amount: searchResult.length })}
                        </td>
                    </tr>
                    <tr>
                        <td>{localizationHandler.get("star-system-distance")}</td>
                        <td>{localizationHandler.get("star-system-name")}</td>
                        <td>{localizationHandler.get("station-name")}</td>
                        <td>{localizationHandler.get("station-distance")}</td>
                        <td>{localizationHandler.get("station-type")}</td>
                        <td>{localizationHandler.get("landing-pad")}</td>
                        <td>{localizationHandler.get("trade-amount")}</td>
                        <td>{localizationHandler.get("price")}</td>
                        <td>{localizationHandler.get("last-updated")}</td>
                        <td>{localizationHandler.get("controlling-power-short")}</td>
                        <td>{localizationHandler.get("powers")}</td>
                        <td>{localizationHandler.get("powerplay-state-short")}</td>
                    </tr>
                </thead>
                <tbody>{getSearchResult()}</tbody>
                {
                    searchResult.length > displayed &&
                    <tfoot>
                        <tr>
                            <td colSpan={12}>
                                <Button
                                    label={localizationHandler.get("load-more")}
                                    onclick={() => setDisplayed(displayed + PAGE_SIZE)}
                                />
                            </td>
                        </tr>
                    </tfoot>
                }
            </table>
        </div>
    );

    function getSearchResult() {
        return new Stream(searchResult)
            .sorted((a, b) => Order[order].sorter(OrderBy[orderBy].compare(a, b)))
            .limit(displayed)
            .map((offer, index) => {
                return (
                    <tr key={index}>
                        <td>{formatNumber(offer.starSystemDistance, 2)} ly</td>
                        <td>{offer.starName}</td>
                        <td>{offer.locationName}</td>
                        <td>{formatNumber(offer.stationDistance, 2)} ls</td>
                        <td>{offer.locationType}</td>
                        <td>{offer.landingPad}</td>
                        <td>{formatNumber(offer.tradeAmount)} T</td>
                        <td>{formatNumber(offer.price)} Cr.</td>
                        <td>{getLastUpdated(offer.lastUpdated)}</td>
                        <td>{PowerNames[offer.controllingPower]}</td>
                        <td>{new Stream(offer.powers).map(power => PowerNames[power]).join(", ")}</td>
                        <td>{offer.powerplayState}</td>
                    </tr>
                )
            })
            .toList();


        function getLastUpdated(lastUpdated) {
            const luldt = LocalDateTime.fromLocalDateTime(lastUpdated);
            const deltaMs = LocalDateTime.now().getEpoch() - luldt.getEpoch();

            let seconds = Math.floor(deltaMs / 1000);
            let minutes = Math.floor(seconds / 60);
            let hours = Math.floor(minutes / 60);
            let days = Math.floor(hours / 24);

            hours = hours % 24;
            minutes = minutes % 60;
            seconds = seconds % 60;

            const arr = [
                new Entry("days", days),
                new Entry("hours", hours),
                new Entry("minutes", minutes),
                new Entry("seconds", seconds),
            ];

            const displayedArr = new Stream(arr)
                .filter(e => e.value > 0)
                .limit(2)
                .toList();

            if (displayedArr.length == 0) {
                return localizationHandler.get("just-now");
            } else if (displayedArr.length == 1) {
                return localizationHandler.get(displayedArr[0].key, { value: displayedArr[0].value }) + " " + localizationHandler.get("ago-suffix");
            } else {
                return new Stream([
                    localizationHandler.get(displayedArr[0].key, { value: displayedArr[0].value }),
                    localizationHandler.get("and"),
                    localizationHandler.get(displayedArr[1].key, { value: displayedArr[1].value }),
                    localizationHandler.get("ago-suffix"),
                ])
                    .join(" ");
            }
        }
    }
}

export default TradeCommoditiesResult;