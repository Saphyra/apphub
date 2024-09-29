import React, { useEffect, useState } from "react";
import Endpoints from "../../../../../../common/js/dao/dao";
import { useQuery } from "react-query";
import Stream from "../../../../../../common/js/collection/Stream";
import AvailableBuilding from "./available_building/AvailableBuilding";
import { hasValue } from "../../../../../../common/js/Utils";

const AvailableBuildings = ({ surfaceType, planetId, surfaceId, closePage }) => {
    const [availableBuildings, setAvailableBuildings] = useState([]);

    const { data: buildingsData } = useQuery(
        "available-building-" + surfaceType,
        async () => {
            return await Endpoints.SKYXPLORE_DATA_AVAILABLE_BUILDINGS.createRequest(null, { surfaceType: surfaceType })
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
        await Endpoints.SKYXPLORE_BUILDING_CONSTRUCT_NEW.createRequest({ value: selectedBuilding }, { planetId: planetId, surfaceId: surfaceId })
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