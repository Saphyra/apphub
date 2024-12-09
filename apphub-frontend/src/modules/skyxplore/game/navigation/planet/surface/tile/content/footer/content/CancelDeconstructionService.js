import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import LocalizationHandler from "../../../../../../../../../../common/js/LocalizationHandler";
import constructionAreaLocalizationData from "../../../../../../../common/localization/construction_area_localization.json";

const confirmCancelDeconstruction = (
    localizationHandler,
    constructionArea,
    setConfirmationDialogData
) => {
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);

    const confirmationDialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-confirm-cancel-deconstruction",
        localizationHandler.get("cancel-construction-area-deconstruction-title"),
        localizationHandler.get("cancel-construction-area-deconstruction-content", { constructionArea: constructionAreaLocalizationHandler.get(constructionArea.dataId) }),
        [
            <Button
                key="cancel-deconstruction"
                id="skyxplore-game-planet-cancel-deconstruction-button"
                label={localizationHandler.get("cancel-deconstruction")}
                onclick={() => cancelDeconstruction(constructionArea.deconstruction.deconstructionId, setConfirmationDialogData)}
            />,
            <Button
                key="continue-construction"
                id="skyxplore-game-planet-continue-deconstruction-button"
                label={localizationHandler.get("continue-deconstruction")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    );

    setConfirmationDialogData(confirmationDialogData);
}

const cancelDeconstruction = async (deconstructionId, setConfirmationDialogData) => {
    await SKYXPLORE_PLANET_SURFACE_CANCEL_DECONSTRUCT_CONSTRUCTION_AREA.createRequest(null, { deconstructionId: deconstructionId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmCancelDeconstruction;