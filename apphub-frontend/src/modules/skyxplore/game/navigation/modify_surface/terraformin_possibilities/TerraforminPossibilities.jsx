import React, { useEffect, useState } from "react";
import { useQuery } from "react-query";
import Stream from "../../../../../../common/js/collection/Stream";
import TerraformingPossibility from "./terraformin_possibility/TerraformingPossibility";
import { hasValue } from "../../../../../../common/js/Utils";
import { SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import { SKYXPLORE_GAME_TERRAFORM_SURFACE } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";

const TerraformingPossibilities = ({ surfaceType, planetId, surfaceId, closePage }) => {
    const [terraformingPossibilities, setTerraformingPossibilities] = useState([]);

    const { data: terraformingData } = useQuery(
        "terraforming-possibility-" + surfaceType,
        async () => {
            return await SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES.createRequest(null, { surfaceType: surfaceType })
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
        }
    );

    useEffect(
        () => {
            if (hasValue(terraformingData)) {
                setTerraformingPossibilities(terraformingData);
            }
        },
        [terraformingData]
    );

    const terraform = async (targetSurfaceType) => {
        await SKYXPLORE_GAME_TERRAFORM_SURFACE.createRequest({ value: targetSurfaceType }, { planetId: planetId, surfaceId: surfaceId })
            .send();

        closePage();
    }

    const getContent = () => {
        return new Stream(terraformingPossibilities)
            .map(terraformingPossibility => <TerraformingPossibility
                key={terraformingPossibility.surfaceType}
                surfaceType={terraformingPossibility.surfaceType}
                constructionRequirements={terraformingPossibility.constructionRequirements}
                terraformCallback={() => terraform(terraformingPossibility.surfaceType)}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-modify-surface-terraforming-possibilities">
            {getContent()}
        </div>
    );
}

export default TerraformingPossibilities;