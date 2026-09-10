import { hasValue } from "common/js/Utils";
import { SKYXPLORE_GAME_GET_GAME_ID, SKYXPLORE_GAME_PAGE } from "./game/SkyXploreGameEndpoints";
import { SKYXPLORE_LOBBY_IS_IN_LOBBY, SKYXPLORE_LOBBY_PAGE } from "./lobby/SkyXploreLobbyEndpoints";
import { SKYXPLORE_MAIN_MENU_PAGE } from "./main_menu/SkyXploreMainMenuEndpoints";
import { SKYXPLORE_CHARACTER_PAGE, SKYXPLORE_PLATFORM_HAS_CHARACTER } from "./character/SkyXploreCharacterEndpoints";

const redirectToCharacterIfNotPresent = async (setDisplaySpinner) => {
    const response = await SKYXPLORE_PLATFORM_HAS_CHARACTER.createRequest()
        .send(setDisplaySpinner);

    if (!response.value) {
        window.location.href = SKYXPLORE_CHARACTER_PAGE;
    }
}

const redirectToLobbyIfInOne = async (setDisplaySpinner) => {
    const response = await SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send(setDisplaySpinner);

    if (response.value) {
        window.location.href = SKYXPLORE_LOBBY_PAGE;
    }
}

const redirectToMainMenuIfNotInLobby = async (setDisplaySpinner) => {
    const response = await SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send(setDisplaySpinner);

    if (!response.value) {
        window.location.href = SKYXPLORE_MAIN_MENU_PAGE;
    }
}

const redirectToGameIfInOne = async (setDisplaySpinner) => {
    const response = await SKYXPLORE_GAME_GET_GAME_ID.createRequest()
        .send(setDisplaySpinner)

    if (hasValue(response.value)) {
        window.location.href = SKYXPLORE_GAME_PAGE;
    }
}

const redirectToMainMenuIfNotInGame = async (setDisplaySpinner) => {
    const response = await SKYXPLORE_GAME_GET_GAME_ID.createRequest()
        .send(setDisplaySpinner)

    if (!hasValue(response.value)) {
        window.location.href = SKYXPLORE_MAIN_MENU_PAGE;
    }
}

const forMainMenu = (setDisplaySpinner) => {
    redirectToCharacterIfNotPresent(setDisplaySpinner);
    redirectToLobbyIfInOne(setDisplaySpinner);
    redirectToGameIfInOne(setDisplaySpinner);
}

const forCharacter = async (setDisplaySpinner) => {
    redirectToLobbyIfInOne(setDisplaySpinner);
    redirectToGameIfInOne(setDisplaySpinner);
}

const forLobby = (setDisplaySpinner) => {
    redirectToMainMenuIfNotInLobby(setDisplaySpinner);
}

const forGame = (setDisplaySpinner) => {
    redirectToMainMenuIfNotInGame(setDisplaySpinner);
}

const Redirection = {
    forMainMenu: forMainMenu,
    forCharacter: forCharacter,
    forLobby: forLobby,
    forGame: forGame
}

export default Redirection;