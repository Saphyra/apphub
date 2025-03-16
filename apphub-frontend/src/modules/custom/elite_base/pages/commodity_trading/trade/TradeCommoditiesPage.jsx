import React, { useEffect, useState } from "react";
import localizationData from "./trade_commodities_page_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import StarSelector from "../../../common/component/star_selector/StarSelector";
import { cacheAndUpdate, cachedOrDefault, formatNumber, hasValue } from "../../../../../../common/js/Utils";
import CommoditySelector from "../../../common/component/commodity_selector/CommoditySelector";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import NumberInput from "../../../../../../common/component/input/NumberInput";
import LabelWrappedInputField from "../../../../../../common/component/input/LabelWrappedInputField";
import PostLabeledInputField from "../../../../../../common/component/input/PostLabeledInputField";
import InputField from "../../../../../../common/component/input/InputField";
import LandingPad from "../../../common/component/landing_pad_selector/LandingPad";
import LandingPadSelector from "../../../common/component/landing_pad_selector/LandingPadSelector";
import LastUpdateSelector from "../../../common/component/last_update_selector/LastUpdateSelector";
import PowerRelation from "../../../common/component/power_selector/PowerRelation";
import PowerSelector from "../../../common/component/power_selector/PowerSelector";
import useCache from "../../../../../../common/hook/Cache";
import { ELITE_BASE_COMMODITY_TRADING_TRADE, ELITE_BASE_GET_POWERPLAY_STATES } from "../../../common/EliteBaseEndpoints";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import Stream from "../../../../../../common/js/collection/Stream";
import Button from "../../../../../../common/component/input/Button";
import "./trade_commodities_page.css";
import MapStream from "../../../../../../common/js/collection/MapStream";
import PowerNames from "../../../common/localization/PowerNames";
import LocalDateTime from "../../../../../../common/js/date/LocalDateTime";
import Entry from "../../../../../../common/js/collection/Entry";
import Spinner from "../../../../../../common/component/Spinner";
import ErrorHandler from "../../../../../../common/js/dao/ErrorHandler";

//TODO split
const TradeCommoditiesPage = ({ tradeMode }) => {
    const CACHE_KEY_MIN_PRICE = "eliteBaseCommodityTradingMinPrice";
    const CACHE_KEY_MAX_PRICE = "eliteBaseCommodityTradingMaxPrice";
    const CACHE_KEY_MAX_STAR_SYSTEM_DISTANCE = "eliteBaseCommodityTradingMaxStarSystemDistance";
    const CACHE_KEY_MAX_STATION_DISTANCE = "eliteBaseCommodityTradingMaxStationDistance";
    const CACHE_KEY_INCLUDE_UNKNOWN_STATION_DISTANCE = "eliteBaseCommodityTradingIncludeUnknownStationDistance";
    const CACHE_KEY_INCLUDE_UNKNOWN_LANDING_PAD = "eliteBaseCommodityTradingIncludeUnknownLandingPad";
    const CACHE_KEY_MIN_LANDING_PAD = "eliteBaseCommodityTradingMinLandingPad";
    const CACHE_KEY_MAX_TIME_SINCE_LAST_UPDATED = "eliteBaseCommodityTradingMaxTimeSinceLastUpdated";
    const CACHE_KEY_INCLUDE_SURFACE_STATIONS = "eliteBaseCommodityTradingIncludeSurfaceStations";
    const CACHE_KEY_INCLUDE_FLEET_CARRIERS = "eliteBaseCommodityTradingIncludeFleetCarriers";
    const CACHE_KEY_MIN_TRADE_AMOUNT = "eliteBaseCommodityTradingMinTradeAmount";
    const CACHE_KEY_CONTROLLING_POWER = "eliteBaseCommodityTradingControllingPower";
    const CACHE_KEY_CONTROLLING_POWER_RELATION = "eliteBaseCommodityTradingControllingPowerRelation";
    const CACHE_KEY_POWERS = "eliteBaseCommodityTradingPowers";
    const CACHE_KEY_POWERS_RELATION = "eliteBaseCommodityTradingPowersRelation";
    const CACHE_KEY_POWERPLAY_STATE = "eliteBaseCommodityTradingPowerplayState";
    const PAGE_SIZE = 20;

    const localizationHandler = new LocalizationHandler(localizationData);
    const [powerplayStates, setPowerplayStates] = useState([]);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    const [starId, setStarId] = useState(null);
    const [commodity, setCommodity] = useState(null);
    const [minPrice, setMinPrice] = useState(cachedOrDefault(CACHE_KEY_MIN_PRICE, 1));
    const [maxPrice, setMaxPrice] = useState(cachedOrDefault(CACHE_KEY_MAX_PRICE, 1_000_000));
    const [maxStarSystemDistance, setMaxStarSystemDistance] = useState(cachedOrDefault(CACHE_KEY_MAX_STAR_SYSTEM_DISTANCE, 100));
    const [maxStationDistance, setMaxStationDistance] = useState(cachedOrDefault(CACHE_KEY_MAX_STATION_DISTANCE, 1_000_000));
    const [includeUnknownStationDistance, setIncludeUnknownStationDistance] = useState(cachedOrDefault(CACHE_KEY_INCLUDE_UNKNOWN_STATION_DISTANCE, true, v => v === "true"));
    const [minLandingPad, setMinLandingPad] = useState(cachedOrDefault(CACHE_KEY_MIN_LANDING_PAD, LandingPad.MEDIUM));
    const [includeUnknownLandingPad, setIncludeUnknownLandingPad] = useState(cachedOrDefault(CACHE_KEY_INCLUDE_UNKNOWN_LANDING_PAD, true, v => v === "true"));
    const [maxTimeSinceLastUpdated, setMaxTimeSinceLastUpdated] = useState(cachedOrDefault(CACHE_KEY_MAX_TIME_SINCE_LAST_UPDATED, "P3650D"));
    const [includeSurfaceStations, setIncludeSurfaceStations] = useState(cachedOrDefault(CACHE_KEY_INCLUDE_SURFACE_STATIONS, false, v => v === "true"));
    const [includeFleetCarriers, setIncludeFleetCarriers] = useState(cachedOrDefault(CACHE_KEY_INCLUDE_FLEET_CARRIERS, false, v => v === "true"));
    const [minTradeAmount, setMinTradeAmount] = useState(cachedOrDefault(CACHE_KEY_MIN_TRADE_AMOUNT, 1));

    //Powerplay
    const [controllingPowers, setControllingPowers] = useState(cachedOrDefault(CACHE_KEY_CONTROLLING_POWER, [], v => JSON.parse(v)));
    const [controllingPowerRelation, setControllingPowerRelation] = useState(cachedOrDefault(CACHE_KEY_CONTROLLING_POWER_RELATION, PowerRelation.ANY));
    const [powers, setPowers] = useState(cachedOrDefault(CACHE_KEY_POWERS, [], v => JSON.parse(v)));
    const [powersRelation, setPowersRelation] = useState(cachedOrDefault(CACHE_KEY_POWERS_RELATION, PowerRelation.ANY));
    const [powerplayState, setPowerplayState] = useState(cachedOrDefault(CACHE_KEY_POWERPLAY_STATE, ""));

    //Search result
    const [searchResult, setSearchResult] = useState(null);
    const [orderBy, setOrderBy] = useState(OrderBy.SYSTEM_DISTANCE.key);
    const [order, setOrder] = useState(Order.ASCENDING.key);
    const [displayed, setDisplayed] = useState(PAGE_SIZE);

    useCache("elite-base-powerplay-states", ELITE_BASE_GET_POWERPLAY_STATES.createRequest(), setPowerplayStates);

    useEffect(() => setDisplayed(PAGE_SIZE), [searchResult]);

    return (
        <div className="elite-base-page">
            <fieldset>
                <legend>todo</legend>

                <CommoditySelector
                    commodity={commodity}
                    setCommodity={setCommodity}
                />

                <LastUpdateSelector
                    lastUpdate={maxTimeSinceLastUpdated}
                    setLastUpdate={v => cacheAndUpdate(CACHE_KEY_MAX_TIME_SINCE_LAST_UPDATED, v, setMaxTimeSinceLastUpdated)}
                />
            </fieldset>

            <fieldset>
                <legend>{localizationHandler.get("location")}</legend>

                <StarSelector
                    starId={starId}
                    setStarId={setStarId}
                />

                <div>
                    <LabelWrappedInputField
                        preLabel={localizationHandler.get("max-star-system-distance") + ":"}
                        postLabel={"ly"}
                        inputField={<NumberInput
                            placeholder={localizationHandler.get("max-star-system-distance")}
                            value={maxStarSystemDistance}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MAX_STAR_SYSTEM_DISTANCE, v, setMaxStarSystemDistance)}
                            min={0}
                        />
                        }
                    />
                </div>

                <div>
                    <LabelWrappedInputField
                        preLabel={localizationHandler.get("max-station-distance") + ":"}
                        postLabel={"ls"}
                        inputField={<NumberInput
                            placeholder={localizationHandler.get("max-station-distance")}
                            value={maxStationDistance}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MAX_STATION_DISTANCE, v, setMaxStationDistance)}
                            min={0}
                        />
                        }
                    />
                </div>

                <LandingPadSelector
                    landingPad={minLandingPad}
                    setLandingPad={v => cacheAndUpdate(CACHE_KEY_MIN_LANDING_PAD, v, setMinLandingPad)}
                />

                <div>
                    <PostLabeledInputField
                        label={localizationHandler.get("include-surface-stations")}
                        input={<InputField
                            type="checkbox"
                            checked={includeSurfaceStations}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_INCLUDE_SURFACE_STATIONS, v, setIncludeSurfaceStations)}
                        />
                        }
                    />
                </div>

                <div>
                    <PostLabeledInputField
                        label={localizationHandler.get("include-fleet-carriers")}
                        input={<InputField
                            type="checkbox"
                            checked={includeFleetCarriers}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_INCLUDE_FLEET_CARRIERS, v, setIncludeFleetCarriers)}
                        />
                        }
                    />
                </div>

                <div>
                    <PostLabeledInputField
                        label={localizationHandler.get("include-unknown-station-distance")}
                        input={<InputField
                            type="checkbox"
                            checked={includeUnknownStationDistance}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_INCLUDE_UNKNOWN_STATION_DISTANCE, v, setIncludeUnknownStationDistance)}
                        />
                        }
                    />
                </div>

                <div>
                    <PostLabeledInputField
                        label={localizationHandler.get("include-unknown-landing-pad")}
                        input={<InputField
                            type="checkbox"
                            checked={includeUnknownLandingPad}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_INCLUDE_UNKNOWN_LANDING_PAD, v, setIncludeUnknownLandingPad)}
                        />
                        }
                    />
                </div>
            </fieldset>

            <fieldset>
                <legend>{localizationHandler.get("offer")}</legend>

                <div>Display galactic average</div>

                <div>
                    <PreLabeledInputField
                        label={localizationHandler.get("min-price") + ":"}
                        input={<NumberInput
                            placeholder={localizationHandler.get("min-price")}
                            value={minPrice}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MIN_PRICE, v, setMinPrice)}
                            min={0}
                            max={maxPrice}
                        />
                        }
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("max-price") + ":"}
                        input={<NumberInput
                            placeholder={localizationHandler.get("max-price")}
                            value={maxPrice}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MAX_PRICE, v, setMaxPrice)}
                            min={minPrice}
                        />
                        }
                    />
                </div>

                <div>
                    <PreLabeledInputField
                        label={localizationHandler.get("min-trade-amount") + ":"}
                        input={<NumberInput
                            placeholder={localizationHandler.get("min-trade-amount")}
                            value={minTradeAmount}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MIN_TRADE_AMOUNT, v, setMinTradeAmount)}
                            min={0}
                        />
                        }
                    />
                </div>
            </fieldset>

            <fieldset>
                <legend>Powerplay</legend>

                <div>
                    <PowerSelector
                        label={localizationHandler.get("controlling-power")}
                        relation={controllingPowerRelation}
                        setRelation={v => cacheAndUpdate(CACHE_KEY_CONTROLLING_POWER_RELATION, v, setControllingPowerRelation)}
                        selectedPowers={controllingPowers}
                        setSelectedPowers={v => cacheAndUpdate(CACHE_KEY_CONTROLLING_POWER, v, setControllingPowers, v => JSON.stringify(v))}
                    />
                </div>

                <div>
                    <PowerSelector
                        label={localizationHandler.get("powers")}
                        relation={powersRelation}
                        setRelation={v => cacheAndUpdate(CACHE_KEY_POWERS_RELATION, v, setPowersRelation)}
                        selectedPowers={powers}
                        setSelectedPowers={v => cacheAndUpdate(CACHE_KEY_POWERS, v, setPowers, v => JSON.stringify(v))}
                    />
                </div>

                <div>
                    <PreLabeledInputField
                        label={localizationHandler.get("powerplay-state") + ":"}
                        input={<SelectInput
                            value={powerplayState}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_POWERPLAY_STATE, v, setPowerplayState)}
                            options={getPowerplayStateOptions()}
                        />
                        }
                    />
                </div>
            </fieldset>

            <div id="elite-base-ct-trade-commodities-search-button-wrapper">
                <Button
                    label={localizationHandler.get("search")}
                    onclick={search}
                />
            </div>

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

            {hasValue(searchResult) &&
                <div>
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
            }

            {displaySpinner && <Spinner />}
        </div>
    );

    function getPowerplayStateOptions() {
        return new Stream(powerplayStates)
            .map(state => new SelectOption(state, state))
            .sorted((a, b) => b.label.localeCompare(a.label))
            .add(new SelectOption(localizationHandler.get("any"), ""))
            .reverse()
            .toList();
    }

    function search() {
        setDisplaySpinner(true);
        doSearch();
    }

    async function doSearch() {
        //TODO validate input

        const request = {
            tradeMode: tradeMode,
            referenceStarId: starId,
            commodity: commodity,
            minPrice: minPrice,
            maxPrice: maxPrice,
            maxStarSystemDistance: maxStarSystemDistance,
            maxStationDistance: maxStationDistance,
            includeUnknownStationDistance: includeUnknownStationDistance,
            minLandingPad: minLandingPad,
            includeUnknownLandingPad: includeUnknownLandingPad,
            maxTimeSinceLastUpdated: maxTimeSinceLastUpdated,
            includeSurfaceStations: includeSurfaceStations,
            includeFleetCarriers: includeFleetCarriers,
            controllingPowers: controllingPowers,
            controllingPowerRelation: controllingPowerRelation,
            powers: powers,
            powersRelation: powersRelation,
            powerplayState: powerplayState == "" ? null : powerplayState,
            minTradeAmount: minTradeAmount,
        }

        const response = await ELITE_BASE_COMMODITY_TRADING_TRADE.createRequest(request)
            .addErrorHandler(new ErrorHandler(() => true, () => setDisplaySpinner(false)))
            .send();

        setSearchResult(response);
        setDisplaySpinner(false);
    }

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
                        <td>{offer.tradeAmount} T</td>
                        <td>{offer.price} Cr.</td>
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

const OrderBy = {
    SYSTEM_DISTANCE: {
        key: "SYSTEM_DISTANCE",
        compare: (a, b) => a.starSystemDistance - b.starSystemDistance
    },
    TRADE_AMOUNT: {
        key: "TRADE_AMOUNT",
        compare: (a, b) => a.tradeAmount - b.tradeAmount
    },
    PRICE: {
        key: "PRICE",
        compare: (a, b) => a.price - b.price
    },
    LAST_UPDATED: {
        key: "LAST_UPDATED",
        compare: (a, b) => b.lastUpdated.localeCompare(a.lastUpdated)
    }
}

const Order = {
    ASCENDING: {
        key: "ASCENDING",
        sorter: i => i
    },
    DESCENDING: {
        key: "DESCENDING",
        sorter: i => -1 * i
    }
}

export default TradeCommoditiesPage;