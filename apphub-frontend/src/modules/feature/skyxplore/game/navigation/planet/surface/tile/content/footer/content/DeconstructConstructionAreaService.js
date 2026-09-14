import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "common/component/input/Button";
import { SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA } from "modules/feature/skyxplore/game/SkyXploreGameEndpoints";

const confirmDeconstructConstructionArea = (
    localizationHandler,
    buildingLocalizationHandler,
    constructionArea,
    setConfirmationDialogData,
    setDisplaySpinner
) => {
    const dialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-surface-confirm-deconstruct-construction-area",
        localizationHandler.get("confirm-deconstruct-construction-area-title"),
        localizationHandler.get("confirm-deconstruct-construction-area-content", { constructionAreaName: buildingLocalizationHandler.get(constructionArea.dataId) }),
        [
            <Button
                key="deconstruct"
                id="skyxplore-game-planet-surface-confirm-deconstruct-construction-area-button"
                label={localizationHandler.get("deconstruct")}
                onclick={() => deconstructConstructionArea(constructionArea.constructionAreaId, setConfirmationDialogData, setDisplaySpinner)}
            />,
            <Button
                key="cancel"
                id="skyxplore-game-planet-surface-confirm-deconstruct-construction-area-cancel-button"
                onclick={() => setConfirmationDialogData(null)}
                label={localizationHandler.get("cancel")}
            />
        ]
    )

    setConfirmationDialogData(dialogData);
}

const deconstructConstructionArea = async (constructionAreaId, setConfirmationDialogData, setDisplaySpinner) => {
    await SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA.createRequest(null, { constructionAreaId: constructionAreaId })
        .send(setDisplaySpinner);

    setConfirmationDialogData(null);
}

export default confirmDeconstructConstructionArea;