import useCache from "common/hook/Cache";
import Stream from "common/js/collection/Stream";
import { SKYXPLORE_DATA_CONSTRUCTION_AREAS } from "modules/feature/skyxplore/SkyXploreDataEndpoints";
import { useState } from "react";
import ConstructionArea from "./ConstructionArea";

const ConstructionAreas = ({ surfaceType, surfaceId, closePage, setDisplaySpinner }) => {
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
                setDisplaySpinner={setDisplaySpinner}
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