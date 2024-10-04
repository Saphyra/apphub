import ConfirmationDialogData from "../../../../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../../../../../common/component/input/Button";
import { SKYXPLORE_BUILDING_DECONSTRUCT } from "../../../../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const confirmDeconstructBuilding = (
    localizationHandler,
    buildingLocalizationHandler,
    surface,
    setConfirmationDialogData,
    planetId
) => {
    const dialogData = new ConfirmationDialogData(
        "skyxplore-game-planet-surface-confirm-deconstruct-building",
        localizationHandler.get("confirm-deconstruct-building-title"),
        localizationHandler.get("confirm-deconstruct-building-content", { buildingName: buildingLocalizationHandler.get(surface.building.dataId) }),
        [
            <Button
                key="deconstruct"
                id="skyxplore-game-planet-surface-confirm-deconstruct-building-button"
                label={localizationHandler.get("deconstruct")}
                onclick={() => deconstructBuilding(planetId, surface, setConfirmationDialogData)}
            />,
            <Button
                key="cancel"
                id="skyxplore-game-planet-surface-confirm-deconstruct-building-cancel-button"
                onclick={() => setConfirmationDialogData(null)}
                label={localizationHandler.get("cancel")}
            />
        ]
    )

    setConfirmationDialogData(dialogData);
}

const deconstructBuilding = async (planetId, surface, setConfirmationDialogData) => {
    await SKYXPLORE_BUILDING_DECONSTRUCT.createRequest(null, { planetId: planetId, buildingId: surface.building.buildingId })
        .send();

    setConfirmationDialogData(null);
}

export default confirmDeconstructBuilding;