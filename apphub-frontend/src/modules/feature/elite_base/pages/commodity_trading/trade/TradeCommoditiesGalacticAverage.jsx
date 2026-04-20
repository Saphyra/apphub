import React, { useState } from "react";
import useCache from "../../../../../../common/hook/Cache";
import { ELITE_BASE_COMMODITY_TRADING_COMMODITIES_AVERAGE_PRICE } from "../../../common/EliteBaseEndpoints";
import { formatNumber, hasValue } from "../../../../../../common/js/Utils";

const TradeCommoditiesGalacticAverage = ({ localizationHandler, commodity }) => {
    const [averagePrice, setAveragePrice] = useState(0);

    useCache("elite-base-commodity-average-pricei" + commodity, ELITE_BASE_COMMODITY_TRADING_COMMODITIES_AVERAGE_PRICE.createRequest(null, {commodityName: commodity}), setAveragePrice, hasValue(commodity));

    return (
        <span>
            <span>{localizationHandler.get("average-price")}: </span>
            <span>{formatNumber(averagePrice)}</span>
            <span> Cr.</span>
        </span>
    );
}

export default TradeCommoditiesGalacticAverage;