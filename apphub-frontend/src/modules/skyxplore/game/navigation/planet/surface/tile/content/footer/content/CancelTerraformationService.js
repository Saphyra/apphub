import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_GAME_CANCEL_TERRAFORMATION } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const confirmCancelTerraformation = (
    localizationHandler,
    surfaceLocalizationHandler,
    surface,
    setConfirmationDialogData,
    planetId
) => {
    const confirmationDialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-confirm-cancel-terraformation",
        localizationHandler.get("cancel-terraformation-title"),
        localizationHandler.get("cancel-terraformation-content", { surfaceType: surfaceLocalizationHandler.get(surface.terraformation.data) }),
        [
            <Button
                key="cancel"
                id="skyxplore-game-planet-cancel-terraformation-button"
                label={localizationHandler.get("cancel-terraformation")}
                onclick={() => cancelTerraformation(planetId, surface, setConfirmationDialogData)}
            />,
            <Button
                key="continue"
                id="skyxplore-game-planet-continue-terraformation-button"
                label={localizationHandler.get("continue-terraformation")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    );

    setConfirmationDialogData(confirmationDialogData);
}

const cancelTerraformation = async (planetId, surface, setConfirmationDialogData) => {
    await SKYXPLORE_GAME_CANCEL_TERRAFORMATION.createRequest(null, { planetId: planetId, surfaceId: surface.surfaceId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmCancelTerraformation;