import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const confirmDeconstructConstructionArea = (
    localizationHandler,
    buildingLocalizationHandler,
    constructionArea,
    setConfirmationDialogData
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
                onclick={() => deconstructConstructionArea(constructionArea.constructionAreaId, setConfirmationDialogData)}
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

const deconstructConstructionArea = async (constructionAreaId, setConfirmationDialogData) => {
    await SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA.createRequest(null, { constructionAreaId: constructionAreaId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmDeconstructConstructionArea;