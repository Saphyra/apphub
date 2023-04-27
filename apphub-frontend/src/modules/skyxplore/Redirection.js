import Constants from "../../common/js/Constants";
import Endpoints from "../../common/js/dao/dao";

const redirectToCharacterIfNotPresent = async () => {
    const response = await Endpoints.SKYXPLORE_PLATFORM_HAS_CHARACTER.createRequest()
        .send();

    if (!response.value) {
        window.location.href = Constants.SKYXPLORE_CHARACTER_PAGE;
    }
}

const redirectToLobbyIfInOne = async () => {
    const response = await Endpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send();

    if (response.value) {
        window.location.href = Constants.SKYXPLORE_LOBBY_PAGE;
    }
}

const redirectToMainMenuIfNotInLobby = async () => {
    const response = await Endpoints.SKYXPLORE_LOBBY_IS_IN_LOBBY.createRequest()
        .send();

    if (!response.value) {
        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
    }
}

const redirectToGameIfInOne = async () => {
    const response = await Endpoints.SKYXPLORE_IS_USER_IN_GAME.createRequest()
        .send()

    if (response.value) {
        window.location.href = Constants.SKYXPLORE_GAME_PAGE;
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
    redirectToCharacterIfNotPresent();
    redirectToMainMenuIfNotInLobby();
    redirectToGameIfInOne();
}

const Redirection = {
    forMainMenu: forMainMenu,
    forCharacter: forCharacter,
    forLobby: forLobby
}

export default Redirection;