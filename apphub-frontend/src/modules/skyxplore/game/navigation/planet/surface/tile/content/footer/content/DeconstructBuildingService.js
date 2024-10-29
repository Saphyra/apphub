import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_BUILDING_DECONSTRUCT } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const confirmDeconstructConstructionArea = (
    localizationHandler,
    buildingLocalizationHandler,
    surface,
    setConfirmationDialogData,
    planetId
) => {
    const dialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-surface-confirm-deconstruct-building",
        localizationHandler.get("confirm-deconstruct-construction-area-title"),
        localizationHandler.get("confirm-deconstruct-construction-area-content", { constructionAreaName: buildingLocalizationHandler.get(surface.constructionArea.dataId) }),
        [
            <Button
                key="deconstruct"
                id="skyxplore-game-planet-surface-confirm-deconstruct-construction-area-button"
                label={localizationHandler.get("deconstruct")}
                onclick={() => deconstructConstructionArea(planetId, surface, setConfirmationDialogData)}
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

const deconstructConstructionArea = async (planetId, surface, setConfirmationDialogData) => {
    //TODO update endpoint
    await SKYXPLORE_BUILDING_DECONSTRUCT.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmDeconstructConstructionArea;