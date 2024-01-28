import React, { useEffect, useState } from "react";
import { useQuery } from "react-query";
import Endpoints from "../../../../../../common/js/dao/dao";
import Utils from "../../../../../../common/js/Utils";
import Stream from "../../../../../../common/js/collection/Stream";
import TerraformingPossibility from "./terraformin_possibility/TerraformingPossibility";

const TerraformingPossibilities = ({ surfaceType, planetId, surfaceId, closePage }) => {
    const [terraformingPossibilities, setTerraformingPossibilities] = useState([]);

    const { data: terraformingData } = useQuery(
        "terraforming-possibility-" + surfaceType,
        async () => {
            return await Endpoints.SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES.createRequest(null, { surfaceType: surfaceType })
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
        }
    );

    useEffect(
        () => {
            if (Utils.hasValue(terraformingData)) {
                setTerraformingPossibilities(terraformingData);
            }
        },
        [terraformingData]
    );

    const terraform = async (targetSurfaceType) => {
        await Endpoints.SKYXPLORE_GAME_TERRAFORM_SURFACE.createRequest({ value: targetSurfaceType }, { planetId: planetId, surfaceId: surfaceId })
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