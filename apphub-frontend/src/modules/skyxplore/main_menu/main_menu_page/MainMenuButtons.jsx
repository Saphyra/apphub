import React, { useEffect, useState } from "react";
import Button from "../../../../common/component/input/Button";
import Endpoints from "../../../../common/js/dao/dao";
import Stream from "../../../../common/js/collection/Stream";
import Constants from "../../../../common/js/Constants";
import SavedGame from "./saved_game/SavedGame";
import ConfirmationDialog from "../../../../common/component/ConfirmationDialog";

const MainMenuButtons = ({ localizationHandler, setDisplaynNewGameConfirmationDialog }) => {
    const [displaySavedGames, setDisplaySavedGames] = useState(false);
    const [savedGames, setSavedGames] = useState([]);
    const [gameToDelete, setGameToDelete] = useState(null);

    useEffect(() => loadSavedGames(), [displaySavedGames]);

    const loadSavedGames = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_GET_GAMES.createRequest()
                .send();

            setSavedGames(response);
        }
        displaySavedGames && fetch();
    }

    const askDeleteGame = (savedGame) => {
        setGameToDelete(savedGame);
    }

    const deleteGame = async () => {
        await Endpoints.SKYXPLORE_DELETE_GAME.createRequest(null, { gameId: gameToDelete.gameId })
            .send();

        const copy = new Stream(savedGames)
            .filter(savedGame => savedGame.gameId !== gameToDelete.gameId)
            .toList();

        setSavedGames(copy);
        setGameToDelete(null);
    }

    const getSavedGames = () => {
        if (savedGames.length === 0) {
            return <div className="skyxplore-no-saved-games">
                {localizationHandler.get("no-saved-games")}
            </div>
        }

        return new Stream(savedGames)
            .sorted((a, b) => -1 * (a.lastPlayed - b.lastPlayed))
            .map(savedGame =>
                <SavedGame
                    savedGame={savedGame}
                    localizationHandler={localizationHandler}
                    deleteGameCallback={askDeleteGame}
                />
            )
            .toList();
    }

    return (
        <div id="skyxplore-main-menu-buttons">
            <Button
                id="skyxplore-new-game-button"
                className="skyxplore-main-menu-button"
                onclick={() => setDisplaynNewGameConfirmationDialog(true)}
                label={localizationHandler.get("new-game")}
            />

            <Button
                id="skyxplore-load-game-button"
                className="skyxplore-main-menu-button"
                onclick={() => { setDisplaySavedGames(!displaySavedGames) }}
                label={localizationHandler.get("load-game")}
            />

            {displaySavedGames && getSavedGames()}

            <Button
                id="skyxplore-edit-character-button"
                className="skyxplore-main-menu-button"
                onclick={() => window.location.href = Constants.SKYXPLORE_CHARACTER_PAGE}
                label={localizationHandler.get("edit-character")}
            />

            <Button
                id="skyxplore-home-button"
                className="skyxplore-main-menu-button"
                onclick={() => window.location.href = Constants.MODULES_PAGE}
                label={localizationHandler.get("home")}
            />

            {gameToDelete !== null &&
                <ConfirmationDialog
                    id="skyxplore-delete-game"
                    title={localizationHandler.get("delete-game")}
                    content={localizationHandler.get("confirm-delete-game", { gameName: gameToDelete.gameName })}
                    choices={[
                        <Button
                            key="delete"
                            id="skyxplore-delete-game-confirm-button"
                            label={localizationHandler.get("delete")}
                            onclick={deleteGame}
                        />,
                        <Button
                            key="cancel"
                            id="skyxplore-delete-game-cancel-button"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setGameToDelete(null)}
                        />
                    ]}
                />
            }
        </div>
    );
}

export default MainMenuButtons;