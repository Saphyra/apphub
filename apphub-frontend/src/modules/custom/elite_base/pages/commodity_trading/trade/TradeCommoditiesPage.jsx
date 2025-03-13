import React, { useEffect, useState } from "react";
import localizationData from "./trade_commodities_page_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import StarSelector from "../../../common/component/star_selector/StarSelector";
import { formatNumber, hasValue } from "../../../../../../common/js/Utils";
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

//TODO split
const TradeCommoditiesPage = ({ tradeMode }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const [powerplayStates, setPowerplayStates] = useState([]);

    const [starId, setStarId] = useState(null);
    const [commodity, setCommodity] = useState(null);
    const [minPrice, setMinPrice] = useState(1);
    const [maxPrice, setMaxPrice] = useState(1_000_000);
    const [maxStarSystemDistance, setMaxStarSystemDistance] = useState(100);
    const [maxStationDistance, setMaxStationDistance] = useState(1_000_000);
    const [includeUnknownStationDistance, setIncludeUnknownStationDistance] = useState(true);
    const [minLandingPad, setMinLandingPad] = useState(LandingPad.MEDIUM);
    const [includeUnknownLandingPad, setIncludeUnknownLandingPad] = useState(true);
    const [maxTimeSinceLastUpdated, setMaxTimeSinceLastUpdated] = useState("P3650D");
    const [includeSurfaceStations, setIncludeSurfaceStations] = useState(false);
    const [includeFleetCarriers, setIncludeFleetCarriers] = useState(false);
    const [minTradeAmount, setMinTradeAmount] = useState(1);

    //Powerplay
    const [controllingPowers, setControllingPowers] = useState([]);
    const [controllingPowerRelation, setControllingPowerRelation] = useState(PowerRelation.ANY);
    const [powers, setPowers] = useState([]);
    const [powersRelation, setPowersRelation] = useState(PowerRelation.ANY);
    const [powerplayState, setPowerplayState] = useState("");

    //Search result
    const [searchResult, setSearchResult] = useState([]);
    const [orderBy, setOrderBy] = useState(OrderBy.SYSTEM_DISTANCE.key);
    const [order, setOrder] = useState(Order.ASCENDING.key);


    useCache("elite-base-powerplay-states", ELITE_BASE_GET_POWERPLAY_STATES.createRequest(), setPowerplayStates);

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
                    setLastUpdate={setMaxTimeSinceLastUpdated}
                />
            </fieldset>

            <fieldset>
                <legend>{localizationHandler.get("location")}</legend>

                <StarSelector
                    starId={starId}
                    setStarId={setStarId}
                    locked={hasValue(starId)}
                />

                <div>
                    <LabelWrappedInputField
                        preLabel={localizationHandler.get("max-star-system-distance") + ":"}
                        postLabel={"ly"}
                        inputField={<NumberInput
                            placeholder={localizationHandler.get("max-star-system-distance")}
                            value={maxStarSystemDistance}
                            onchangeCallback={setMaxStarSystemDistance}
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
                            onchangeCallback={setMaxStationDistance}
                            min={0}
                        />
                        }
                    />
                </div>

                <LandingPadSelector
                    landingPad={minLandingPad}
                    setLandingPad={setMinLandingPad}
                />

                <div>
                    <PostLabeledInputField
                        label={localizationHandler.get("include-surface-stations")}
                        input={<InputField
                            type="checkbox"
                            checked={includeSurfaceStations}
                            onchangeCallback={setIncludeSurfaceStations}
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
                            onchangeCallback={setIncludeFleetCarriers}
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
                            onchangeCallback={setIncludeUnknownStationDistance}
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
                            onchangeCallback={setIncludeUnknownLandingPad}
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
                            onchangeCallback={setMinPrice}
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
                            onchangeCallback={setMaxPrice}
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
                            onchangeCallback={setMinTradeAmount}
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
                        setRelation={setControllingPowerRelation}
                        selectedPowers={controllingPowers}
                        setSelectedPowers={setControllingPowers}
                    />
                </div>

                <div>
                    <PowerSelector
                        label={localizationHandler.get("powers")}
                        relation={powersRelation}
                        setRelation={setPowersRelation}
                        selectedPowers={powers}
                        setSelectedPowers={setPowers}
                    />
                </div>

                <div>
                    <PreLabeledInputField
                        label={localizationHandler.get("powerplay-state") + ":"}
                        input={<SelectInput
                            value={powerplayState}
                            onchangeCallback={setPowerplayState}
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

            {searchResult.length > 0 &&
                <div>
                    <table id="elite-base-ct-trade-commodities-search-result-table" className="formatted-table selectable">
                        <thead>
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
                    </table>
                </div>
            }
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

    async function search() {
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
            .send();

        setSearchResult(response);
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