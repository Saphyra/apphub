import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "common/component/input/Button";
import { SKYXPLORE_GAME_CANCEL_TERRAFORMATION } from "modules/feature/skyxplore/game/SkyXploreGameEndpoints";

const confirmCancelTerraformation = (
    localizationHandler,
    surfaceLocalizationHandler,
    surface,
    setConfirmationDialogData,
    planetId,
    setDisplaySpinner
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
                onclick={() => cancelTerraformation(planetId, surface, setConfirmationDialogData, setDisplaySpinner)}
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

const cancelTerraformation = async (planetId, surface, setConfirmationDialogData, setDisplaySpinner) => {
    await SKYXPLORE_GAME_CANCEL_TERRAFORMATION.createRequest(null, { planetId: planetId, surfaceId: surface.surfaceId })
        .send(setDisplaySpinner);

    setConfirmationDialogData(null);
}

export default confirmCancelTerraformation;