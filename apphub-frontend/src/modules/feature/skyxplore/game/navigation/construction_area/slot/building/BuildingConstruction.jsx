import Button from "common/component/input/Button";
import ProgressBar from "common/component/progress_bar/ProgressBar";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_CONSTRUCTION_OF_BUILDING_MODULE } from "modules/feature/skyxplore/game/SkyXploreGameEndpoints";

const BuildingConstruction = ({ construction, localizationHandler, setBuildings, setDisplaySpinner }) => {
    const cancelConstruction = async () => {
        const response = await SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_CANCEL_CONSTRUCTION_OF_BUILDING_MODULE.createRequest(null, { constructionId: construction.constructionId })
            .send(setDisplaySpinner);

        setBuildings(response);
    }

    const getContent = () => {
        return (
            <span>
                {localizationHandler.get("constructing")}
                <span> </span>
                <Button
                    className="skyxplore-game-construction-area-building-cancel-construction-button"
                    label="X"
                    title={localizationHandler.get("cancel-construction")}
                    onclick={cancelConstruction}
                />
            </span>
        );
    }

    return (
        <div>
            <ProgressBar
                currentPoints={construction.currentWorkPoints}
                targetPoints={construction.requiredWorkPoints}
                content={getContent()}
            />
        </div>
    );
}

export default BuildingConstruction;