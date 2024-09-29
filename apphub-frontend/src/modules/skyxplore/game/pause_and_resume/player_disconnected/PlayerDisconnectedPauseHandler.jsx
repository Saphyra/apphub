import React, { useEffect, useState } from "react";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import localizationData from "./player_disconnected_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";
import { hasValue } from "../../../../../common/js/Utils";

const PlayerDisconnectedPauseHandler = ({ lastEvent, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [dialogDisplayed, setDialogDisplayed] = useState(false);

    useEffect(() => handleEvent(), [lastEvent]);

    const confirm = () => {
        setDialogDisplayed(false);
        setConfirmationDialogData(null);
    }

    const resume = () => {
        setDialogDisplayed(false);
        setConfirmationDialogData(null);

        Endpoints.SKYXPLORE_GAME_PAUSE.createRequest({ value: false })
            .send();
    }

    const handleEvent = () => {
        if (!hasValue(lastEvent)) {
            return;
        }

        switch (lastEvent.eventName) {
            case WebSocketEventName.SKYXPLORE_GAME_PLAYER_DISCONNECTED:
                playerDisconnected(lastEvent);
                break;
            case WebSocketEventName.SKYXPLORE_GAME_PLAYER_RECONNECTED:
                playerReconnected(lastEvent);
                break;
        }
    }

    const playerDisconnected = (lastEvent) => {
        setDialogDisplayed(true);

        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-player-disconnected-dialog",
            localizationHandler.get("player-disconnected-title"),
            localizationHandler.get("player-disconnected-detail", { playerName: lastEvent.payload.value }),
            [
                <Button
                    key="confirm"
                    id="skyxplore-game-player-disconnected-confirm-button"
                    label={localizationHandler.get("confirm")}
                    onclick={confirm}
                />,
                <Button
                    key="resume"
                    id="skyxplore-game-player-disconnected-resume-button"
                    label={localizationHandler.get("resume")}
                    onclick={resume}
                />
            ]
        );

        setConfirmationDialogData(confirmationDialogData);
    }

    const playerReconnected = (lastEvent) => {
        if (!dialogDisplayed) {
            return;
        }

        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-player-reconnected-dialog",
            localizationHandler.get("player-reconnected-title"),
            localizationHandler.get("player-reconnected-detail", { playerName: lastEvent.payload.value }),
            [
                <Button
                    key="close"
                    id="skyxplore-game-player-reconnected-close-button"
                    label={localizationHandler.get("close")}
                    onclick={confirm}
                />,
                <Button
                    key="resume"
                    id="skyxplore-game-player-reconnected-resume-button"
                    label={localizationHandler.get("resume")}
                    onclick={resume}
                />
            ]
        );

        setConfirmationDialogData(confirmationDialogData);
    }
}

export default PlayerDisconnectedPauseHandler;