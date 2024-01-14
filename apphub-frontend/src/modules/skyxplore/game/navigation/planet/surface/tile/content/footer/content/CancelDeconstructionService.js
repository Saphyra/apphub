import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import Endpoints from "../../../../../../../../../../common/js/dao/dao";

const confirmCancelDeconstruction = (
    localizationHandler,
    buildingLocalizationHandler,
    surface,
    setConfirmationDialogData,
    planetId
) => {
    const confirmationDialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-confirm-cancel-deconstruction",
        localizationHandler.get("cancel-deconstruction-title"),
        localizationHandler.get("cancel-deconstruction-content", { buildingName: buildingLocalizationHandler.get(surface.building.dataId) }),
        [
            <Button
                key="cancel-cdeonstruction"
                id="skyxplore-game-planet-cancel-deconstruction-button"
                label={localizationHandler.get("cancel-deconstruction")}
                onclick={() => cancelDeconstruction(planetId, surface, setConfirmationDialogData)}
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

const cancelDeconstruction = async (planetId, surface, setConfirmationDialogData) => {
    await Endpoints.SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmCancelDeconstruction;