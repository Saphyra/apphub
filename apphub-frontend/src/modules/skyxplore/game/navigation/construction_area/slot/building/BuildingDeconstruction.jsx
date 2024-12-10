import React from "react";
import ProgressBar from "../../../../../../../common/component/progress_bar/ProgressBar";
import Button from "../../../../../../../common/component/input/Button";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_DECONSTRUCTION_OF_BUILDING_MODULE } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const BuildingDeconstruction = ({ deconstruction, localizationHandler, setBuildings }) => {
    const cancelDeconstruction = async () => {
        const response = await SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_DECONSTRUCTION_OF_BUILDING_MODULE.createRequest(null, { deconstructionId: deconstruction.deconstructionId })
            .send();

        setBuildings(response);
    }

    const getContent = () => {
        return (
            <span>
                {localizationHandler.get("deconstructing")}
                <span> </span>
                <Button
                    className="skyxplore-game-construction-area-building-cancel-deconstruction-button"
                    label="X"
                    title={localizationHandler.get("cancel-deconstruction")}
                    onclick={cancelDeconstruction}
                />
            </span>
        );
    }

    return (
        <div>
            <ProgressBar
                currentPoints={deconstruction.currentWorkPoints}
                targetPoints={deconstruction.requiredWorkPoints}
                content={getContent()}
            />
        </div>
    );
}

export default BuildingDeconstruction;