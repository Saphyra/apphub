import React from "react";
import Button from "../../../../common/component/input/Button";
import localizationData from "./exit_game_button_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Constants from "../../../../common/js/Constants";
import { SKYXPLORE_EXIT_GAME, SKYXPLORE_GAME_SAVE } from "../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const ExitGameButton = ({ setConfirmationDialogData, isHost, setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const openConfirmationDialog = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-confirm-exit-confirmation-dialog",
            localizationHandler.get("title"),
            localizationHandler.get(isHost ? "content-host" : "content"),
            [
                <Button
                    key="exit"
                    id="skyxplore-game-confirm-exit-button"
                    label={localizationHandler.get("exit")}
                    onclick={exit}
                />,
                (isHost ?
                    <Button
                        key="save-and-exit"
                        id="skyxplore-game-confirm-save-and-exit-button"
                        label={localizationHandler.get("save-and-exit")}
                        onclick={saveAndExit}
                    />
                    : []),
                <Button
                    key="cancel"
                    id="skyxplore-game-cancel-exit-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        )

        setConfirmationDialogData(confirmationDialogData);
    }

    const exit = async () => {
        await SKYXPLORE_EXIT_GAME.createRequest()
            .send();

        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
    }

    const saveAndExit = async () => {
        setDisplaySpinner(true);

        await SKYXPLORE_GAME_SAVE.createRequest()
            .send();

        exit();
    }

    return <Button
        id="skyxplore-game-exit-button"
        label={localizationHandler.get("exit")}
        onclick={openConfirmationDialog}
    />
}

export default ExitGameButton;