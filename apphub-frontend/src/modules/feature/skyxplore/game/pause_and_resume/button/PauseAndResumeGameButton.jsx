import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./pause_and_resume_localization.json";
import "./pause_and_resume.css";
import Button from "common/component/input/Button";
import { SKYXPLORE_GAME_PAUSE } from "../../SkyXploreGameEndpoints";

const PauseAndResumeGameButton = ({ isHost, paused, setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData)

    const updatePaused = (newStatus) => {
        SKYXPLORE_GAME_PAUSE.createRequest({ value: newStatus })
            .send(setDisplaySpinner);
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