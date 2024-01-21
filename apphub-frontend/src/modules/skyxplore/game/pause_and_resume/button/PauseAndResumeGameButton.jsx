import React, { useEffect, useState } from "react";
import Button from "../../../../../common/component/input/Button";
import Utils from "../../../../../common/js/Utils";
import Endpoints from "../../../../../common/js/dao/dao";
import localizationData from "./pause_and_resume_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./pause_and_resume.css";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";

const PauseAndResumeGameButton = ({ lastEvent }) => {
    const localizationHandler = new LocalizationHandler(localizationData)
    const [paused, setPaused] = useState(true);

    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (!Utils.hasValue(lastEvent)) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_GAME_PAUSED) {
            setPaused(Utils.isTrue(lastEvent.payload));
        }
    }

    const updatePaused = (newStatus) => {
        Endpoints.SKYXPLORE_GAME_PAUSE.createRequest({ value: newStatus })
            .send();
    }

    if (paused) {
        return <Button
            id="skyxplore-game-resume-game-button"
            className=""
            onclick={() => updatePaused(false)}
            label={localizationHandler.get("resume-game")}
        />
    } else {
        return <Button
            id="skyxplore-game-pause-game-button"
            className=""
            onclick={() => updatePaused(true)}
            label={localizationHandler.get("pause-game")}
        />
    }
}

export default PauseAndResumeGameButton;