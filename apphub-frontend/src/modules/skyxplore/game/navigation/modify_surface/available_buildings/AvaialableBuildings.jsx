import React, { useEffect, useState } from "react";
import { useQuery } from "react-query";
import Stream from "../../../../../../common/js/collection/Stream";
import AvailableBuilding from "./available_building/AvailableBuilding";
import { hasValue } from "../../../../../../common/js/Utils";
import { SKYXPLORE_DATA_AVAILABLE_BUILDINGS } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import { SKYXPLORE_BUILDING_CONSTRUCT_NEW } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";

const AvailableBuildings = ({ surfaceType, planetId, surfaceId, closePage }) => {
    const [availableBuildings, setAvailableBuildings] = useState([]);

    const { data: buildingsData } = useQuery(
        "available-building-" + surfaceType,
        async () => {
            return await SKYXPLORE_DATA_AVAILABLE_BUILDINGS.createRequest(null, { surfaceType: surfaceType })
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
        }
    );

    useEffect(
        () => {
            if (hasValue(buildingsData)) {
                setAvailableBuildings(buildingsData);
            }
        },
        [buildingsData]
    );

    const construct = async (selectedBuilding) => {
        await SKYXPLORE_BUILDING_CONSTRUCT_NEW.createRequest({ value: selectedBuilding }, { planetId: planetId, surfaceId: surfaceId })
            .send();

        closePage();
    }

    const getContent = () => {
        return new Stream(availableBuildings)
            .map(buildingDataId => <AvailableBuilding
                key={buildingDataId}
                buildingDataId={buildingDataId}
                surfaceType={surfaceType}
                constructCallback={() => construct(buildingDataId)}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-modify-surface-available-buildings">
            {getContent()}
        </div>
    );
}

export default AvailableBuildings;