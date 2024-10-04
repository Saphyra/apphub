import Constants from "../../common/js/Constants";
import { SKYXPLORE_PLATFORM_HAS_CHARACTER } from "../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import { SKYXPLORE_GAME_GET_GAME_ID } from "../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import { SKYXPLORE_LOBBY_IS_IN_LOBBY } from "../../common/js/dao/endpoints/skyxplore/SkyXploreLobbyEndpoints";
import { hasValue } from "../../common/js/Utils";

const redirectToCharacterIfNotPresent = async () => {
    const response = await SKYXPLORE_PLATFORM_HAS_CHARACTER.createRequest()
        .send();

    if (!response.value) {
        window.location.href = Constants.SKYXPLORE_CHARACTER_PAGE;
    }
}

const redirectToLobbyIfInOne = async () => {
    const response = await SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send();

    if (response.value) {
        window.location.href = Constants.SKYXPLORE_LOBBY_PAGE;
    }
}

const redirectToMainMenuIfNotInLobby = async () => {
    const response = await SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send();

    if (!response.value) {
        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
    }
}

const redirectToGameIfInOne = async () => {
    const response = await SKYXPLORE_GAME_GET_GAME_ID.createRequest()
        .send()

    if (hasValue(response.value)) {
        window.location.href = Constants.SKYXPLORE_GAME_PAGE;
    }
}

const redirectToMainMenuIfNotInGame = async () => {
    const response = await SKYXPLORE_GAME_GET_GAME_ID.createRequest()
        .send()

    if (!hasValue(response.value)) {
        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
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