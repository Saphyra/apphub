import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

//TODO remove
const confirmCancelConstruction = (
    localizationHandler,
    buildingLocalizationHandler,
    surface,
    setConfirmationDialogData,
    planetId
) => {
    const confirmationDialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-confirm-cancel-construction",
        localizationHandler.get("cancel-construction-title"),
        localizationHandler.get("cancel-construction-content", { buildingName: buildingLocalizationHandler.get(surface.building.dataId) }),
        [
            <Button
                key="cancel-construction"
                id="skyxplore-game-planet-cancel-construction-button"
                label={localizationHandler.get("cancel-construction")}
                onclick={() => cancelConstruction(planetId, surface, setConfirmationDialogData)}
            />,
            <Button
                key="continue-construction"
                id="skyxplore-game-planet-continue-construction-button"
                label={localizationHandler.get("continue-construction")}
                onclick={() => setConfirmationDialogData(null)}
            />
        ]
    );

    setConfirmationDialogData(confirmationDialogData);
}

const cancelConstruction = async (planetId, surface, setConfirmationDialogData) => {
    await SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmCancelConstruction;