import React, { useState } from "react";
import useCache from "../../../../../../common/hook/Cache";
import { SKYXPLORE_DATA_CONSTRUCTION_AREAS } from "../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import Stream from "../../../../../../common/js/collection/Stream";
import ConstructionArea from "./ConstructionArea";
import "./construction_area.css";

const ConstructionAreas = ({ surfaceType,  surfaceId, closePage }) => {
    const [constructionAreas, setConstructionAreas] = useState([]);

    useCache(
        "construction-area-" + surfaceType,
        SKYXPLORE_DATA_CONSTRUCTION_AREAS.createRequest(null, { surfaceType: surfaceType }),
        setConstructionAreas
    )

    const getContent = () => {
        return new Stream(constructionAreas)
            .map(constructionArea => <ConstructionArea
                key={constructionArea.id}
                constructionArea={constructionArea}
                surfaceId={surfaceId}
                closePage={closePage}
            />)
            .toList();
    }

    return (
        <div id="kyxplore-game-modify-surface-construction-areas">
            {getContent()}
        </div>
    );
}

export default ConstructionAreas;