import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";

const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

const confirmCancelConstructionAreaConstruction = (
    dataId,
    localizationHandler,
    constructionId,
    setConfirmationDialogData,
) => {
    setConfirmationDialogData(new ConfirmationDialogData(
        "skyxplore-game-planet-confirm-cancel-construction-area-construction",
        localizationHandler.get("cancel-construction-area-construction-title"),
        localizationHandler.get("cancel-construction-area-construction-detail", { constructionArea: constructionAreaLocalizationHandler.get(dataId) }),
        [
            <Button
                key="cancel-construction"
                id="skyxplore-game-planet-cancel-construction-area-construction-button"
                label={localizationHandler.get("cancel-construction")}
                onclick={() => cancelConstruction(constructionId, setConfirmationDialogData)}
            />,
            <Button
                key="constinue-construction"
                id="skyxplore-game-planet-continue-construction-area-construction-button"
                label={localizationHandler.get("continue-construction")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    ));
}

const cancelConstruction = async (constructionId, setConfirmationDialogData) => {
    await SKYXPLORE_PLANET_SURFACE_CANCEL_CONSTRUCTION_AREA_CONSTRUCTION.createRequest(null, { constructionId: constructionId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmCancelConstructionAreaConstruction;