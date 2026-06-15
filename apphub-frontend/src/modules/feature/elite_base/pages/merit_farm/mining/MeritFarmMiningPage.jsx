import Button from "common/component/input/Button";
import useLoader from "common/hook/Loader";
import useRefresh from "common/hook/Refresh";
import { ELITE_BASE_MERIT_FARM_GET_MINE_LOCATIONS } from "modules/feature/elite_base/EliteBaseEndpoints";
import { useEffect, useState } from "react";
import localizationData from "./merit_farm_mining_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import Stream from "common/js/collection/Stream";
import { getLastUpdated } from "modules/feature/elite_base/common/last_update/LastUpdateDisplayCalculator";
import { ReserveLevel } from "./ReserveLevel";
import PreLabeledInputField from "common/component/input/PreLabeledInputField";
import SelectInput, { SelectOption } from "common/component/input/SelectInput";
import NumberInput from "common/component/input/NumberInput";
import "./merit_farm_mining.css";
import LastUpdateSelector from "modules/feature/elite_base/common/component/last_update_selector/LastUpdateSelector";
import { PowerplayActivity } from "./PowerplayActivity";
import { cacheAndUpdate, cachedOrDefault } from "common/js/Utils";

const DISPLAY_SIZE = 20;

const CACHE_KEY_MINIMUM_RESERVE_LEVEL = "elite_base_merit_farm_mining_minimum_reserve_level";
const CACHE_KEY_MINIMUM_PRICE = "elite_base_merit_farm_mining_minimum_price";
const CACHE_KEY_MINIMUM_DEMAND = "elite_base_merit_farm_mining_minimum_demand";
const CACHE_KEY_MAX_TIME_SINCE_LAST_UPDATED = "elite_base_merit_farm_mining_max_time_since_last_updated";
const CACHE_KEY_POWERPLAY_ACTIVITY = "elite_base_merit_farm_mining_powerplay_activity";

const MeritFarmMiningPage = ({ setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [offers, setOffers] = useState([]);
    const [refreshCounter, refresh] = useRefresh(0);
    const [displaySize, setDisplaySize] = useState(DISPLAY_SIZE);

    const [minimumReserveLevel, setMinimumReserveLevel] = useState(cachedOrDefault(CACHE_KEY_MINIMUM_RESERVE_LEVEL, ReserveLevel.UNKNOWN));
    const [minimumPrice, setMinimumPrice] = useState(cachedOrDefault(CACHE_KEY_MINIMUM_PRICE, 0));
    const [minimumDemand, setMinimumDemand] = useState(cachedOrDefault(CACHE_KEY_MINIMUM_DEMAND, 1));
    const [maxTimeSinceLastUpdated, setMaxTimeSinceLastUpdated] = useState(cachedOrDefault(CACHE_KEY_MAX_TIME_SINCE_LAST_UPDATED, "P365D"));
    const [powerplayActivity, setPowerplayActivity] = useState(cachedOrDefault(CACHE_KEY_POWERPLAY_ACTIVITY, PowerplayActivity.ANY));

    useEffect(() => setDisplaySize(DISPLAY_SIZE), [offers]);

    useLoader({
        request: ELITE_BASE_MERIT_FARM_GET_MINE_LOCATIONS.createRequest(
            {
                minimumReserveLevel: minimumReserveLevel,
                minimumPrice: minimumPrice,
                minimumDemand: minimumDemand,
                maxTimeSinceLastUpdated: maxTimeSinceLastUpdated,
                powerplayActivity: powerplayActivity == PowerplayActivity.ANY ? null : powerplayActivity,
            },
            { power: "NAKATO_KAINE" }
        ),
        setDisplaySpinner: setDisplaySpinner,
        mapper: setOffers,
        listener: [refreshCounter],
        condition: () => refreshCounter > 0
    });

    return (
        <div className="elite-base-page">
            <fieldset>
                <div id="elite-base-merit-miner-filters">
                    <PreLabeledInputField
                        label={localizationHandler.get("minimum-reserve-level")}
                        input={<SelectInput
                            value={minimumReserveLevel}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MINIMUM_RESERVE_LEVEL, v, setMinimumReserveLevel)}
                            options={getReserveLevelOptions()}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("minimum-price")}
                        input={<NumberInput
                            id="elite-base-merit-farm-mining-minimum-price"
                            value={minimumPrice}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MINIMUM_PRICE, v, setMinimumPrice)}
                            min={0}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("minimum-demand")}
                        input={<NumberInput
                            id="elite-base-merit-farm-mining-minimum-demand"
                            value={minimumDemand}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_MINIMUM_DEMAND, v, setMinimumDemand)}
                            min={0}
                        />}
                    />

                    <LastUpdateSelector
                        lastUpdate={maxTimeSinceLastUpdated}
                        setLastUpdate={v => cacheAndUpdate(CACHE_KEY_MAX_TIME_SINCE_LAST_UPDATED, v, setMaxTimeSinceLastUpdated)}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("powerplay-activity")}
                        input={<SelectInput
                            value={powerplayActivity}
                            onchangeCallback={v => cacheAndUpdate(CACHE_KEY_POWERPLAY_ACTIVITY, v, setPowerplayActivity)}
                            options={getPowerplayActivityOptions()}
                        />}
                    />
                </div>

                    <Button
                        onclick={() => refresh()}
                        label={localizationHandler.get("load")}
                    />
            </fieldset>

            {offers.length > 0 &&
                <div id="elite-base-merit-miner-offer-count">
                    {localizationHandler.get("offer-count", { count: offers.length })}
                </div>
            }

            <table className="formatted-table" style={{ margin: "auto" }}>
                <thead>
                    <tr>
                        <th>{localizationHandler.get("mine-in")}</th>
                        <th>{localizationHandler.get("reserve-level")}</th>
                        <th>{localizationHandler.get("sell-in")}</th>
                        <th>{localizationHandler.get("at-station")}</th>
                        <th>{localizationHandler.get("commodity")}</th>
                        <th>{localizationHandler.get("price")}</th>
                        <th>{localizationHandler.get("demand")}</th>
                        <th>{localizationHandler.get("last-updated")}</th>
                        <th>{localizationHandler.get("activity")}</th>
                    </tr>
                </thead>
                <tbody>
                    {getData()}
                </tbody>
            </table>

            {offers.length > displaySize &&
                <Button
                    label={localizationHandler.get("display-more")}
                    onclick={() => setDisplaySize(displaySize + DISPLAY_SIZE)}
                />
            }
        </div>
    );

    function getData() {
        return new Stream(offers)
            .sorted((a, b) => b.price - a.price)
            .map(offer => (
                <tr key={offer.sourceStarSystemId + offer.targetStarSystemId + offer.stationId + offer.commodityName}>
                    <td>{offer.sourceStarSystemName}</td>
                    <td>{offer.reserveLevel}</td>
                    <td>{offer.targetStarSystemName}</td>
                    <td>{offer.stationName}</td>
                    <td>{offer.commodityName}</td>
                    <td>{offer.price}</td>
                    <td>{offer.demand}</td>
                    <td>{getLastUpdated(offer.lastUpdate)}</td>
                    <td>{offer.activityType}</td>
                </tr>
            ))
            .toList();
    }

    function getReserveLevelOptions() {
        return new Stream(Object.values(ReserveLevel))
            .map(level => new SelectOption(level, level))
            .toList();
    }

    function getPowerplayActivityOptions() {
        return new Stream(Object.values(PowerplayActivity))
            .map(activity => new SelectOption(activity, activity))
            .toList();
    }
}

export default MeritFarmMiningPage;