import React from "react";
import "./storage_details.css";
import resourceLocalizationData from "../../../../../../common/resource_localization.json";
import LocalizationHandler from "../../../../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../../../../common/js/collection/Stream";

const StorageDetails = ({ resources, localizationHandler }) => {
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

    const getItems = () => {
        return new Stream(resources)
            .map(resource => <ResourceItem
                key={resource.dataId}
                resource={resource}
                localizationHandler={localizationHandler}
                resourceLocalizationHandler={resourceLocalizationHandler}
            />)
            .toList();
    }

    return (
        <div className="skxyplore-game-planet-overview-storage-details">
            {getItems()}
        </div>
    );
}

const ResourceItem = ({ resource, localizationHandler, resourceLocalizationHandler }) => {
    return (
        <div className="skxyplore-game-planet-overview-storage-details-item">
            <div className="skxyplore-game-planet-overview-storage-details-item-name">{resourceLocalizationHandler.get(resource.dataId)}</div>

            <ul>
                <li>
                    <span>{localizationHandler.get("amount")}</span>
                    <span>: (</span>
                    <span className="skxyplore-game-planet-overview-storage-details-item-allocated">{resource.allocatedResourceAmount}</span>
                    <span>) / </span>
                    <span className="skxyplore-game-planet-overview-storage-details-item-actual">{resource.actualAmount}</span>
                </li>

                <li>
                    <span>{localizationHandler.get("reserved-storage")}</span>
                    <span>: </span>
                    <span className="skxyplore-game-planet-overview-storage-details-item-reserved">{resource.reservedStorageAmount}</span>
                </li>
            </ul>
        </div>
    );
}

export default StorageDetails;