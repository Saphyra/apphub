import React, { useEffect, useState } from "react";
import localizationData from "./trade_commodities_page_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import StarSelector from "../../../common/component/star_selector/StarSelector";
import { cacheAndUpdate, cachedOrDefault, hasValue, validate, validateAll } from "../../../../../../common/js/Utils";
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
import Spinner from "../../../../../../common/component/Spinner";
import ErrorHandler from "../../../../../../common/js/dao/ErrorHandler";
import TradeCommoditiesResult from "./TradeCommoditiesResult";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import TradeCommoditiesGalacticAverage from "./TradeCommoditiesGalacticAverage";

export const PAGE_SIZE = 20;

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
    const MIN_PRICE = 1;

    const localizationHandler = new LocalizationHandler(localizationData);
    const [powerplayStates, setPowerplayStates] = useState([]);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    const [starId, setStarId] = useState(null);
    const [commodity, setCommodity] = useState(null);
    const [minPrice, setMinPrice] = useState(cachedOrDefault(CACHE_KEY_MIN_PRICE, MIN_PRICE));
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
    const [displayed, setDisplayed] = useState(PAGE_SIZE);

    useCache("elite-base-powerplay-states", ELITE_BASE_GET_POWERPLAY_STATES.createRequest(), setPowerplayStates);

    useEffect(() => setDisplayed(PAGE_SIZE), [searchResult]);

    return (
        <div className="elite-base-page">
            <fieldset>
                <legend>{localizationHandler.get("offer")}</legend>

                <CommoditySelector
                    commodity={commodity}
                    setCommodity={setCommodity}
                />

                <div>
                    <PreLabeledInputField
                        label={localizationHandler.get("min-price") + ":"}
                        input={<NumberInput
                            placeholder={localizationHandler.get("min-price")}
                            value={minPrice}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MIN_PRICE, v, setMinPrice)}
                            min={MIN_PRICE}
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

                    <TradeCommoditiesGalacticAverage
                        localizationHandler={localizationHandler}
                        commodity={commodity}
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

            {hasValue(searchResult) &&
                <TradeCommoditiesResult
                    localizationHandler={localizationHandler}
                    searchResult={searchResult}
                    displayed={displayed}
                    setDisplayed={setDisplayed}
                />
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
        setDisplayed(PAGE_SIZE);
        setDisplaySpinner(true);
        doSearch();
    }

    async function doSearch() {
        try {
            const validationResult = validateAll(
                [
                    () => validate(() => hasValue(starId), localizationHandler.get("star-not-selected")),
                    () => validate(() => hasValue(commodity), localizationHandler.get("commodity-not-selected")),
                    () => validate(() => minPrice >= MIN_PRICE, localizationHandler.get("min-price-too-low")),
                    () => validate(() => maxPrice >= minPrice, localizationHandler.get("max-price-too-low")),
                ]
            );

            if (hasValue(validationResult)) {
                NotificationService.showError(validationResult);
                return;
            }

            const request = {
                tradeMode: tradeMode,
                referenceStarId: starId,
                itemName: commodity,
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
        } finally {
            setDisplaySpinner(false);
        }
    }
}

export default TradeCommoditiesPage;