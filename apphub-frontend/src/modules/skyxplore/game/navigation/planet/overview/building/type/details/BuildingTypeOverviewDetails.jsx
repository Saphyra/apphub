import React from "react";
import localizationData from "./building_type_overview_details_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import "./building_type_overview_details.css";
import Stream from "../../../../../../../../../common/js/collection/Stream";

const BuildingTypeOverviewDetails = ({ buildingDetails }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingLocalizationHandler = {
        get: (key) => key
    };

    const getBuildings = () => {
        return new Stream(buildingDetails)
            .sorted((a, b) => buildingLocalizationHandler.get(a.dataId).localeCompare(buildingLocalizationHandler.get(b.dataId)))
            .map(buildingDetail => <BuildingDetailRow
                key={buildingDetail.dataId}
                dataId={buildingDetail.dataId}
                name={buildingLocalizationHandler.get(buildingDetail.dataId)}
                totalLevel={buildingDetail.levelSum}
                usedSlots={buildingDetail.usedSlots}
            />)
            .toList();
    }

    return (
        <div className="skxyplore-game-planet-overview-building-details-item">
            <table className="skxyplore-game-planet-overview-building-details-table formatted-table">
                <thead>
                    <tr>
                        <th>{localizationHandler.get("building")}</th>
                        <th>{localizationHandler.get("total-level")}</th>
                        <th>{localizationHandler.get("used-slots")}</th>
                    </tr>
                </thead>
                <tbody>
                    {getBuildings()}
                </tbody>
            </table>
        </div>
    );
}

const BuildingDetailRow = ({ dataId, name, totalLevel, usedSlots }) => {
    return (
        <tr className={"skyxplore-game-planet-overview-building-details-item-" + dataId}>
            <td>{name}</td>
            <td className="skyxplore-game-planet-overview-building-details-item-total-level">{totalLevel}</td>
            <td className="skyxplore-game-planet-overview-building-details-item-used-slots">{usedSlots}</td>
        </tr>
    );
}

export default BuildingTypeOverviewDetails;