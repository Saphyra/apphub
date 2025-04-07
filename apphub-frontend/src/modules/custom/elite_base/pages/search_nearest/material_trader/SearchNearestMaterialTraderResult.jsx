import React from "react";
import Stream from "../../../../../../common/js/collection/Stream";
import { formatNumber } from "../../../../../../common/js/Utils";
import MaterialTraderOverride from "./MaterialTraderOverride";

const SearchNearestMaterialTraderResult = ({ localizationHandler, searchResult, reload }) => {
    return (
        <div id="elite-base-sn-material-trader-search-result">
            <table className="formatted-table selectable">
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
                    key={record.starId}
                    localizationHandler={localizationHandler}
                    record={record}
                    reload={reload}
                />
            )
            .toList();
    }
}

const SearchResultRow = ({ localizationHandler, record, reload }) => {
    return (
        <tr>
            <td>
                <span>{record.materialType}</span>
                <MaterialTraderOverride
                    localizationHandler={localizationHandler}
                    record={record}
                    reload={reload}
                />
            </td>
            <td>{formatNumber(record.distanceFromReference, 2)}</td>
            <td>{record.starName}</td>
            <td>{record.stationName}</td>
            <td>{formatNumber(record.distanceFromStar, 2)}</td>
        </tr>
    )
}

export default SearchNearestMaterialTraderResult;