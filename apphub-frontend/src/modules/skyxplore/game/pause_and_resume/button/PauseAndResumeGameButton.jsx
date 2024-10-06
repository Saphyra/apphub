import React, { useEffect, useState } from "react";
import Button from "../../../../../common/component/input/Button";
import localizationData from "./pause_and_resume_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./pause_and_resume.css";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";
import { hasValue, isTrue } from "../../../../../common/js/Utils";
import { SKYXPLORE_GAME_PAUSE } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const PauseAndResumeGameButton = ({ lastEvent, isHost }) => {
    const localizationHandler = new LocalizationHandler(localizationData)
    const [paused, setPaused] = useState(true);

    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (!hasValue(lastEvent)) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_GAME_PAUSED) {
            setPaused(isTrue(lastEvent.payload));
        }
    }

    const updatePaused = (newStatus) => {
        SKYXPLORE_GAME_PAUSE.createRequest({ value: newStatus })
            .send();
    }

    if (isHost) {
        if (paused) {
            return <Button
                id="skyxplore-game-resume-game-button"
                onclick={() => updatePaused(false)}
                label={localizationHandler.get("resume-game")}
            />
        } else {
            return <Button
                id="skyxplore-game-pause-game-button"
                onclick={() => updatePaused(true)}
                label={localizationHandler.get("pause-game")}
            />
        }
    } else if (paused) {
        return <Button
            id="skyxplore-game-paused"
            label={localizationHandler.get("paused")}
            disabled={true}
        />
    }
}

export default PauseAndResumeGameButton;