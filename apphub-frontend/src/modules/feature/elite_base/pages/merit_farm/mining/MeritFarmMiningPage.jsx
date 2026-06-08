import Button from "common/component/input/Button";
import useLoader from "common/hook/Loader";
import useRefresh from "common/hook/Refresh";
import { ELITE_BASE_MERIT_FARM_GET_MINE_LOCATIONS } from "modules/feature/elite_base/EliteBaseEndpoints";
import { useState } from "react";
import localizationData from "./merit_farm_mining_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import Stream from "common/js/collection/Stream";

const MeritFarmMiningPage = ({ setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [offers, setOffers] = useState([]);
    const [refreshCounter, refresh] = useRefresh(0);


    useLoader({
        request: ELITE_BASE_MERIT_FARM_GET_MINE_LOCATIONS.createRequest(null, { power: "NAKATO_KAINE" }),
        setDisplaySpinner: setDisplaySpinner,
        mapper: setOffers,
        listener: [refreshCounter],
        condition: () => refreshCounter > 0
    });

    //TODO add filters
    //TODO localize table heads
    //TODO format table
    return (
        <div className="elite-base-page">
            <Button
                onclick={() => refresh()}
                label={localizationHandler.get("refresh")}
            />

            <table className="formatted-table">
                <thead>
                    <tr>
                        <th>Mine in</th>
                        <th>Sell in</th>
                        <th>at Station</th>
                        <th>Commodity</th>
                        <th>Price</th>
                        <th>Demand</th>
                        <th>Activity</th>
                    </tr>
                </thead>
                <tbody>
                    {getData()}
                </tbody>
            </table>
        </div>
    );

    function getData() {
        return new Stream(offers)
            .sorted((a, b) => b.price - a.price)
            .map(offer => (
                <tr key={offer.sourceStarSystemId + offer.targetStarSystemId + offer.stationId + offer.commodityName}>
                    <td>{offer.sourceStarSystemName}</td>
                    <td>{offer.targetStarSystemName}</td>
                    <td>{offer.stationName}</td>
                    <td>{offer.commodityName}</td>
                    <td>{offer.price}</td>
                    <td>{offer.demand}</td>
                    <td>{offer.activityType}</td>
                </tr>
            ))
            .toList();
    }
}

export default MeritFarmMiningPage;