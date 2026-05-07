import { hasValue } from "common/js/Utils";
import { SKYXPLORE_GAME_GET_GAME_ID, SKYXPLORE_GAME_PAGE } from "./game/SkyXploreGameEndpoints";
import { SKYXPLORE_LOBBY_IS_IN_LOBBY, SKYXPLORE_LOBBY_PAGE } from "./lobby/SkyXploreLobbyEndpoints";
import { SKYXPLORE_MAIN_MENU_PAGE } from "./main_menu/SkyXploreMainMenuEndpoints";
import { SKYXPLORE_CHARACTER_PAGE, SKYXPLORE_PLATFORM_HAS_CHARACTER } from "./character/SkyXploreCharacterEndpoints";

const redirectToCharacterIfNotPresent = async () => {
    const response = await SKYXPLORE_PLATFORM_HAS_CHARACTER.createRequest()
        .send();

    if (!response.value) {
        window.location.href = SKYXPLORE_CHARACTER_PAGE;
    }
}

const redirectToLobbyIfInOne = async () => {
    const response = await SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send();

    if (response.value) {
        window.location.href = SKYXPLORE_LOBBY_PAGE;
    }
}

const redirectToMainMenuIfNotInLobby = async () => {
    const response = await SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send();

    if (!response.value) {
        window.location.href = SKYXPLORE_MAIN_MENU_PAGE;
    }
}

const redirectToGameIfInOne = async () => {
    const response = await SKYXPLORE_GAME_GET_GAME_ID.createRequest()
        .send()

    if (hasValue(response.value)) {
        window.location.href = SKYXPLORE_GAME_PAGE;
    }
}

const redirectToMainMenuIfNotInGame = async () => {
    const response = await SKYXPLORE_GAME_GET_GAME_ID.createRequest()
        .send()

    if (!hasValue(response.value)) {
        window.location.href = SKYXPLORE_MAIN_MENU_PAGE;
    }
}

const forMainMenu = () => {
    redirectToCharacterIfNotPresent();
    redirectToLobbyIfInOne();
    redirectToGameIfInOne();
}

const forCharacter = async () => {
    redirectToLobbyIfInOne();
    redirectToGameIfInOne();
}

const forLobby = () => {
    redirectToMainMenuIfNotInLobby();
}

const forGame = () => {
    redirectToMainMenuIfNotInGame();
}

const Redirection = {
    forMainMenu: forMainMenu,
    forCharacter: forCharacter,
    forLobby: forLobby,
    forGame: forGame
}

export default Redirection;