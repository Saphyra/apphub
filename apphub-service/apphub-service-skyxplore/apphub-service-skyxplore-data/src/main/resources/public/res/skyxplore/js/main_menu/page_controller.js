scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/common/js/animation/roll.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/common/js/validation_util.js");
scriptLoader.loadScript("/res/skyxplore/js/main_menu/friend_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/main_menu/invitation_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/main_menu/create_lobby_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/main_menu/games_controller.js");

(function PageController(){
    window.ids = {
        searchFriendInput: "new-friend-name",
        friendSearchResultWrapper: "new-friend-search-result-wrapper",
        queryStringTooShort: "query-string-too-short",
        characterNotFound: "no-character-found",
        friendSearchResult: "new-friend-search-result",
        sentFriendRequestsContainer: "outgoing-friend-request-list",
        incomingFriendRequestsContainer: "incoming-friend-request-list",
        friendListContainer: "friend-list",
        friendsContainer: "friends-container",
        invitationContainer: "invitations",
        lobbyNameInput: "lobby-name",
        createLobbyButton: "create-lobby-button",
        invalidLobbyName: "invalid-lobby-name",
        gamesWrapper: "games-wrapper",
        games: "games",
    }

    const webSocketConnection = new WebSocketConnection(Mapping.getEndpoint("WS_CONNECTION_SKYXPLORE_MAIN_MENU"));

    window.pageController = new function(){
        this.showMainMenu = function(){
            switchTab("main-page", "main-menu");
        }
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "main_menu"}));
        webSocketConnection.addHandlers(invitationController.createHandlers())
            .addHandlers(friendController.createHandlers())
            .connect();
    });
})();