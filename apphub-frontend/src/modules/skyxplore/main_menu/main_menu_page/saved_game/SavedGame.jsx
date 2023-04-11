import React from "react";
import Button from "../../../../../common/component/input/Button";
import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";
import "./saved_game.css"

const SavedGame = ({ localizationHandler, savedGame, deleteGameCallback }) => {
    const loadSavedGame = async () => {
        await Endpoints.SKYXPLORE_LOBBY_LOAD_GAME.createRequest(null, { gameId: savedGame.gameId })
            .send();

        window.location.href = Constants.SKYXPLORE_LOBBY_PAGE
    }

    const deleteGame = (e) => {
        e.stopPropagation();
        deleteGameCallback(savedGame);
    }

    return (
        <div
            className="skyxplore-saved-game button"
            onClick={loadSavedGame}
        >
            {savedGame.gameName}

            <Button
                className="skyxplore-delete-game-button"
                label={localizationHandler.get("delete")}
                onclick={deleteGame}
            />
        </div>
    );
}

export default SavedGame;