import React from "react";
import Stream from "../../../../../../common/js/collection/Stream";
import { formatNumber } from "../../../../../../common/js/Utils";

const SearchNearestMaterialTraderResult = ({ localizationHandler, searchResult }) => {
    return (
        <div id="elite-base-sn-material-trader-search-result">
            <table className="formatted-table">
                <thead>
                    <tr>
                        <th>{localizationHandler.get("material-type")}</th>
                        <th>{localizationHandler.get("distance-from-reference")}</th>
                        <th>{localizationHandler.get("star-system-name")}</th>
                        <th>{localizationHandler.get("station-name")}</th>
                        <th>{localizationHandler.get("station-distance")}</th>
                    </tr>
                </thead>
                <tbody>
                    {getContent()}
                </tbody>
            </table>
        </div>
    );

    function getContent() {
        return new Stream(searchResult)
            .map(record =>
                <SearchResultRow
                    key={searchResult.starId}
                    record={record}
                />
            )
            .toList();
    }
}

const SearchResultRow = ({ record }) => {
    return (
        <tr>
            <td>{record.materialType}</td>
            <td>{formatNumber(record.distanceFromReference, 2)}</td>
            <td>{record.starName}</td>
            <td>{record.stationName}</td>
            <td>{formatNumber(record.distanceFromStar, 2)}</td>
        </tr>
    )
}

export default SearchNearestMaterialTraderResult;